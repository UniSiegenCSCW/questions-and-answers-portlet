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

import com.liferay.portal.kernel.service.ServiceContext;
import de.sidate.questions_and_answers.model.Category;
import de.sidate.questions_and_answers.model.Content;
import de.sidate.questions_and_answers.model.Question;
import de.sidate.questions_and_answers.service.base.ContentLocalServiceBaseImpl;

import java.util.List;

/**
 * The implementation of the content local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link de.sidate.questions_and_answers.service.ContentLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ContentLocalServiceBaseImpl
 * @see de.sidate.questions_and_answers.service.ContentLocalServiceUtil
 */
@ProviderType
public class ContentLocalServiceImpl extends ContentLocalServiceBaseImpl {

    public List<Content> getContents(long groupId) {
        return contentPersistence.findByGroupId(groupId);
    }

    public Content addContent(String text, ServiceContext serviceContext) {

        long groupId = serviceContext.getScopeGroupId();
        long contentId = counterLocalService.increment();
        Content content = contentPersistence.create(contentId);

        content.setUuid(serviceContext.getUuid());
        content.setGroupId(groupId);
        content.setExpandoBridgeAttributes(serviceContext);
        content.setText(text);

        contentPersistence.update(content);

        return content;
    }
}