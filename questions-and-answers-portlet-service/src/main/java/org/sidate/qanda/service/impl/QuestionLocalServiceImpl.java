/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package org.sidate.qanda.service.impl;

import aQute.bnd.annotation.ProviderType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Validator;
import org.sidate.qanda.exception.EmptyQuestionTextException;
import org.sidate.qanda.exception.EmptyQuestionTitleException;
import org.sidate.qanda.exception.NoSufficientAccessRightsException;
import org.sidate.qanda.model.Answer;
import org.sidate.qanda.model.Question;
import org.sidate.qanda.service.QuestionLocalService;
import org.sidate.qanda.service.QuestionLocalServiceUtil;
import org.sidate.qanda.service.base.QuestionLocalServiceBaseImpl;


import java.util.Date;
import java.util.List;

import static org.sidate.qanda.service.permission.PermissionCheckerHelper.*;

/**
 * The implementation of the question local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link QuestionLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see QuestionLocalServiceBaseImpl
 * @see QuestionLocalServiceUtil
 */
@ProviderType
public class QuestionLocalServiceImpl extends QuestionLocalServiceBaseImpl {

    private static Log log = LogFactoryUtil.getLog(QuestionLocalServiceImpl.class);


    public List<Question> getQuestions(long groupId) throws NoSufficientAccessRightsException {
        checkIfUserIsSignedIn();
        return questionPersistence.findByGroupId(groupId);
    }

    public void setCorrectAnswer(long answerId, long questionId, ServiceContext serviceContext) throws PortalException {
        checkPermission(Question.class, questionId, "SET_AS_CORRECT");

        Question question = questionPersistence.fetchByPrimaryKey(questionId);
        question.setCorrectAnswerId(answerId);
        questionPersistence.update(question);
    }
    
    public Answer getCorrectAnswer(long questionId) {
        Question question = questionPersistence.fetchByPrimaryKey(questionId);
        long answerId = question.getCorrectAnswerId();
        return answerPersistence.fetchByPrimaryKey(answerId);
    }

    public Question addQuestion(String title, String text, ServiceContext serviceContext) throws
            EmptyQuestionTitleException, EmptyQuestionTextException, NoSufficientAccessRightsException {

        checkIfUserIsSignedIn();

        // Validation
        if (Validator.isNull(title)) throw new EmptyQuestionTitleException();
        if (Validator.isNull(text)) throw new EmptyQuestionTextException();

        long groupId = serviceContext.getScopeGroupId();
        long questionId = counterLocalService.increment();
        Date createDate = serviceContext.getCreateDate();
        Date modifiedDate = serviceContext.getModifiedDate();
        String uuid = serviceContext.getUuid();
        Question question = questionPersistence.create(questionId);
        long userId = serviceContext.getUserId();

        question.setUserName(UserLocalServiceUtil.fetchUser(userId).getFullName());
        question.setUuid(uuid);
        question.setCreateDate(createDate);
        question.setModifiedDate(modifiedDate);
        question.setUserId(userId);
        question.setGroupId(groupId);
        question.setExpandoBridgeAttributes(serviceContext);
        question.setCorrectAnswerId(0);

        questionPersistence.update(question);

        try {
            assetEntryLocalService.updateEntry(
                    serviceContext.getUserId(), question.getGroupId(), question.getCreateDate(), question.getModifiedDate(),
                    Question.class.getName(), question.getPrimaryKey(), question.getUuid(), 0,
                    serviceContext.getAssetCategoryIds(), serviceContext.getAssetTagNames(), true, true, null, null,
                    null, null, ContentTypes.TEXT_HTML, title, text, "",
                    null, null, 0, 0, 0D);

            Indexer<Question> indexer = IndexerRegistryUtil.nullSafeGetIndexer(Question.class);
            System.out.println(indexer);
            indexer.reindex(question);
        } catch (PortalException e) {
            e.printStackTrace();
        }
        return question;
    }

    public void editQuestion(long questionId, String title, String text, ServiceContext serviceContext) throws PortalException {

        // Validation
        if (Validator.isNull(title)) throw new EmptyQuestionTitleException();
        if (Validator.isNull(text)) throw new EmptyQuestionTextException();

        Question question = questionPersistence.fetchByPrimaryKey(questionId);

        question.setEditedBy(serviceContext.getUserId());
        question.setEditedDate(new Date());
        questionPersistence.update(question);

        assetEntryLocalService.updateEntry(
                serviceContext.getUserId(), question.getGroupId(), question.getCreateDate(), question.getModifiedDate(),
                Question.class.getName(), question.getPrimaryKey(), question.getUuid(), 0,
                serviceContext.getAssetCategoryIds(), serviceContext.getAssetTagNames(), true, true, null, null, null, null, ContentTypes.TEXT_HTML, title,
                text, null, null, null, 0, 0, 0D);

        Indexer<Question> indexer = IndexerRegistryUtil.nullSafeGetIndexer(Question.class);
        indexer.reindex(question);
    }

    @Deprecated
    public Question deleteQuestion(Question question) {
        return super.deleteQuestion(question);
    }

    @Deprecated
    public Question deleteQuestion(long questionId) throws PortalException {
        return super.deleteQuestion(questionId);
    }

    public Question deleteQuestion(long questionId, ServiceContext serviceContext) throws PortalException {
        List<Answer> answers = answerLocalService.getAnswersForQuestion(questionId);
        for (Answer answer : answers) {
            answerLocalService.deleteAnswer(answer.getAnswerID(), serviceContext);
        }

		Question question = super.deleteQuestion(questionId);
        assetEntryLocalService.deleteEntry(Question.class.getName(), questionId);
        Indexer<Question> indexer = IndexerRegistryUtil.nullSafeGetIndexer(Question.class);
        indexer.delete(question);

        return question;
    }



}