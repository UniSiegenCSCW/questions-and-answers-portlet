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

package de.sidate.questions_and_answers.service.impl;

import aQute.bnd.annotation.ProviderType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Validator;
import de.sidate.questions_and_answers.exception.QuestionTextException;
import de.sidate.questions_and_answers.exception.QuestionTitleException;
import de.sidate.questions_and_answers.model.Answer;
import de.sidate.questions_and_answers.model.Question;
import de.sidate.questions_and_answers.service.base.QuestionLocalServiceBaseImpl;

import java.util.Date;
import java.util.List;

/**
 * The implementation of the question local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link de.sidate.questions_and_answers.service.QuestionLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see QuestionLocalServiceBaseImpl
 * @see de.sidate.questions_and_answers.service.QuestionLocalServiceUtil
 */
@ProviderType
public class QuestionLocalServiceImpl extends QuestionLocalServiceBaseImpl {

    public List<Question> getQuestions(long groupId) {
        return questionPersistence.findByGroupId(groupId);
    }

    public void setCorrectAnswer(long answerId, long questionId) {
        Question question = questionPersistence.fetchByPrimaryKey(questionId);
        question.setCorrectAnswerId(answerId);
        questionPersistence.update(question);
    }
    
    public Answer getCorrectAnswer(long questionId) {
        Question question = questionPersistence.fetchByPrimaryKey(questionId);
        long answerId = question.getCorrectAnswerId();
        return answerPersistence.fetchByPrimaryKey(answerId);
    }

    public Question addQuestion(long userId, String title, String text, ServiceContext serviceContext) throws QuestionTitleException, QuestionTextException {

        // Validation
        if (Validator.isNull(title)) throw new QuestionTitleException();
        if (Validator.isNull(text)) throw new QuestionTextException();

        long groupId = serviceContext.getScopeGroupId();
        long questionId = counterLocalService.increment();
        Date createDate = serviceContext.getCreateDate();
        Date modifiedDate = serviceContext.getModifiedDate();
        String uuid = serviceContext.getUuid();
        Question question = questionPersistence.create(questionId);
        String[] tagNames = serviceContext.getAssetTagNames();
        long[] categoryIds = serviceContext.getAssetCategoryIds();

        question.setUuid(uuid);
        question.setCreateDate(createDate);
        question.setModifiedDate(modifiedDate);
        question.setUserId(userId);
        question.setGroupId(groupId);
        question.setExpandoBridgeAttributes(serviceContext);
        question.setTitle(title);
        question.setText(text);

        questionPersistence.update(question);

        try {
            assetEntryLocalService.updateEntry(
                    userId, question.getGroupId(), question.getCreateDate(), question.getModifiedDate(),
                    Question.class.getName(), question.getPrimaryKey(), question.getUuid(), 0,
                    categoryIds, tagNames, true,
                    true, null, null,
                    null, ContentTypes.TEXT_HTML, question.getTitle(),
                    "Question Description appears here", null, null, null,
                    0, 0, 0D);

            // Indexing
            Indexer<Question> indexer = IndexerRegistryUtil.nullSafeGetIndexer(Question.class);
            indexer.reindex(question);

        } catch (PortalException e) {
            e.printStackTrace();
        }


        return question;
    }
}