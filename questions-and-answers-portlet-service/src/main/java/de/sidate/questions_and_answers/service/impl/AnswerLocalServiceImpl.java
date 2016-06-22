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
import de.sidate.questions_and_answers.exception.AnswerTextException;
import de.sidate.questions_and_answers.model.Answer;
import de.sidate.questions_and_answers.service.base.AnswerLocalServiceBaseImpl;
import de.sidate.questions_and_answers.service.persistence.AnswerPersistence;

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
    public List<Answer> getAnswers(long groupId) {
        return answerPersistence.findByGroupId(groupId);
    }

    public Answer addQuestion(long userId, String text, ServiceContext serviceContext) throws AnswerTextException {

        // Validation
        if (Validator.isNull(text)) throw new AnswerTextException();

        long groupId = serviceContext.getScopeGroupId();
        long answerId = counterLocalService.increment();
        Date createDate = serviceContext.getCreateDate();
        Date modifiedDate = serviceContext.getModifiedDate();
        String uuid = serviceContext.getUuid();
        Answer answer = answerPersistence.create(answerId);
        String[] tagNames = serviceContext.getAssetTagNames();
        long[] categoryIds = serviceContext.getAssetCategoryIds();

        answer.setUuid(uuid);
        answer.setCreateDate(createDate);
        answer.setModifiedDate(modifiedDate);
        answer.setUserId(userId);
        answer.setGroupId(groupId);
        answer.setExpandoBridgeAttributes(serviceContext);
        answer.setText(text);

        answerPersistence.update(answer);

        try {
            assetEntryLocalService.updateEntry(
                    userId, answer.getGroupId(), answer.getCreateDate(), answer.getModifiedDate(),
                    Answer.class.getName(), answer.getPrimaryKey(), answer.getUuid(), 0,
                    categoryIds, tagNames, true,
                    true, null, null,
                    null, ContentTypes.TEXT_HTML, "Answer Title",
                    "Question Description appears here", null, null, null,
                    0, 0, 0D);

            // Indexing
            Indexer<Answer> indexer = IndexerRegistryUtil.nullSafeGetIndexer(Answer.class);
            indexer.reindex(answer);

        } catch (PortalException e) {
            e.printStackTrace();
        }


        return answer;
    }
}