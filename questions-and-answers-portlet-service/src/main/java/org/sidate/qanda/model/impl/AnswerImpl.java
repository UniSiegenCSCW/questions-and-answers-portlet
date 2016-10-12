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

package org.sidate.qanda.model.impl;

import aQute.bnd.annotation.ProviderType;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.ratings.kernel.model.RatingsStats;
import com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil;
import org.sidate.qanda.model.Answer;
import org.sidate.qanda.model.Question;
import org.sidate.qanda.model.util.TimeDiffFormatter;

import java.util.Date;

/**
 * The extended model implementation for the Answer service. Represents a row in the &quot;SIDATE_Answer&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * Helper methods and all application logic should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link org.sidate.qanda.model.Answer} interface.
 * </p>
 *
 * @author Brian Wing Shun Chan
 */
@ProviderType
public class AnswerImpl extends AnswerBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. All methods that expect a answer model instance should use the {@link org.sidate.qanda.model.Answer} interface instead.
	 */
	public AnswerImpl() {
	}

	public double getRating() {
		RatingsStats ratingsStats = RatingsStatsLocalServiceUtil.getStats(Answer.class.getName(),
				this.getAnswerID());
		return ratingsStats.getTotalScore();
	}

	public String getTimeSinceCreated() {
		Date created = this.getCreateDate();
		return TimeDiffFormatter.format(created);
	}

	public String getTimeSinceEdited() {
		Date edited = this.getEditedDate();
		return TimeDiffFormatter.format(edited);
	}

	public String getText() throws PortalException {
		return AssetEntryLocalServiceUtil.getEntry(Answer.class.getName(), this.getAnswerID()).getDescription();
	}
}