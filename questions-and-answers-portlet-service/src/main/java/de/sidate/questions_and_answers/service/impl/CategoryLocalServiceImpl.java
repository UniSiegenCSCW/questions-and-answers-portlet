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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import de.sidate.questions_and_answers.model.Category;
import de.sidate.questions_and_answers.service.base.CategoryLocalServiceBaseImpl;

import java.util.List;

/**
 * The implementation of the category local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link de.sidate.questions_and_answers.service.CategoryLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see CategoryLocalServiceBaseImpl
 * @see de.sidate.questions_and_answers.service.CategoryLocalServiceUtil
 */
@ProviderType
public class CategoryLocalServiceImpl extends CategoryLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Always use {@link de.sidate.questions_and_answers.service.CategoryLocalServiceUtil} to access the category local service.
	 */

    public List<Category> getCategories(long categoryId) {
        return categoryPersistence.findByGroupId(categoryId);
    }

    public Category addCategory(String name, ServiceContext serviceContext, String color) {

        long groupId = serviceContext.getScopeGroupId();
        long categoryId = counterLocalService.increment();
        Category category = categoryPersistence.create(categoryId);

        category.setUuid(serviceContext.getUuid());
        category.setGroupId(groupId);
        category.setExpandoBridgeAttributes(serviceContext);
        category.setName(name);
        category.setColor(color);

        categoryPersistence.update(category);

        return category;
    }
}