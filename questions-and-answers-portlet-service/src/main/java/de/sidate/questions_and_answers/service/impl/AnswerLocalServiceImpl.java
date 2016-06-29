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

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.persistence.AssetTagFinderUtil;
import com.liferay.asset.kernel.service.persistence.AssetTagPersistence;
import com.liferay.asset.kernel.service.persistence.AssetTagUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Validator;
import de.sidate.questions_and_answers.exception.AnswerTextValidationException;
import de.sidate.questions_and_answers.exception.EmptyAnswerTextException;
import de.sidate.questions_and_answers.model.Answer;
import de.sidate.questions_and_answers.model.Question;
import de.sidate.questions_and_answers.service.base.AnswerLocalServiceBaseImpl;

import java.util.Date;
import java.util.List;

/**
 * The implementation of the answer local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link de.sidate.questions_and_answers.service.AnswerLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AnswerLocalServiceBaseImpl
 * @see de.sidate.questions_and_answers.service.AnswerLocalServiceUtil
 */
@ProviderType
public class AnswerLocalServiceImpl extends AnswerLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Always use {@link de.sidate.questions_and_answers.service.AnswerLocalServiceUtil} to access the answer local service.
	 */

    public List<Answer> getAnswersForQuestion(long questionId) {
        return answerPersistence.findByQuestionId(questionId);
    }

    public Answer addAnswer(String text, long questionId, ServiceContext serviceContext) throws PortalException {

        // Validation
        if (Validator.isNull(text)) throw new EmptyAnswerTextException();

        long groupId = serviceContext.getScopeGroupId();
        long answerId = counterLocalService.increment();
        Date createDate = serviceContext.getCreateDate();
        Date modifiedDate = serviceContext.getModifiedDate();
        String uuid = serviceContext.getUuid();
        Answer answer = answerPersistence.create(answerId);

        answer.setUuid(uuid);
        answer.setCreateDate(createDate);
        answer.setModifiedDate(modifiedDate);
        answer.setUserId(serviceContext.getUserId());
        answer.setGroupId(groupId);
        answer.setExpandoBridgeAttributes(serviceContext);
        answer.setText(text);
        answer.setQuestionId(questionId);

        answerPersistence.update(answer);

        try {
            assetEntryLocalService.updateEntry(
                    serviceContext.getUserId(), answer.getGroupId(), answer.getCreateDate(), answer.getModifiedDate(),
                    Question.class.getName(), answer.getPrimaryKey(), answer.getUuid(), 0,
                    serviceContext.getAssetCategoryIds(), serviceContext.getAssetTagNames(), true, true, null, null,
                    null, null, ContentTypes.TEXT_HTML, "Title appears here", "Question Description appears here",
                    null, null, null, 0, 0, 0D);

            // Indexing
            Indexer<Answer> indexer = IndexerRegistryUtil.nullSafeGetIndexer(Answer.class);
            indexer.reindex(answer);

        } catch (PortalException e) {
            e.printStackTrace();
        }

        return answer;
    }

    public void editAnswer(long answerId, String text, ServiceContext serviceContext) throws PortalException {
        Answer answer = answerPersistence.fetchByPrimaryKey(answerId);

        answer.setText(text);

        answerPersistence.update(answer);

        assetEntryLocalService.updateEntry(
                serviceContext.getUserId(), answer.getGroupId(), answer.getCreateDate(), answer.getModifiedDate(),
                Question.class.getName(), answer.getPrimaryKey(), answer.getUuid(), 0,
                serviceContext.getAssetCategoryIds(), serviceContext.getAssetTagNames(), true, true, null, null, null,
                null, ContentTypes.TEXT_HTML, "Title appears here", "Question Description appears here", null, null,
                null, 0, 0, 0D);

        Indexer<Answer> indexer = IndexerRegistryUtil.nullSafeGetIndexer(Answer.class);
        indexer.reindex(answer);
    }
}