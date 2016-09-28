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
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.ratings.kernel.model.RatingsStats;
import com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil;
import org.sidate.qanda.model.Question;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * The extended model implementation for the Question service. Represents a row in the &quot;SIDATE_Question&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * Helper methods and all application logic should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link org.sidate.qanda.model.Question} interface.
 * </p>
 *
 * @author Brian Wing Shun Chan
 */
@ProviderType
public class QuestionImpl extends QuestionBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. All methods that expect a question model instance should use the {@link org.sidate.qanda.model.Question} interface instead.
	 */

    private static Log log = LogFactoryUtil.getLog(QuestionImpl.class);

	public QuestionImpl() {
	}

    public String getTimeSinceCreated() {
        long diffSeconds = 0;
        Date created = this.getCreateDate();
        diffSeconds = (new Date().getTime() - created.getTime()) / 1000;

        if (diffSeconds > 864000) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd. MMM yyyy");
            return "am " + sdf.format(created);

        } else if (diffSeconds >= 86400) {
            int value = (int) diffSeconds / 60 / 60 / 24;
            if(value == 1) {
                return "vor " + value + " Tag";
            } else {
                return "vor " + value + " Tagen";
            }
        } else if (diffSeconds >= 3600) {
            int value = (int) diffSeconds / 60 / 60;
            if(value == 1) {
                return "vor " + value + " Stunde";
            } else {
                return "vor " + value + " Stunden";
            }
        } else if (diffSeconds >= 60) {
            int value = (int) diffSeconds / 60;
            if(value == 1) {
                return "vor " + value + " Minute";
            } else {
                return "vor " + value + " Minuten";
            }
        } else {
            int value = (int) diffSeconds;
            if(value == 1) {
                return "vor " + value + " Sekunde";
            } else {
                return "vor " + value + " Sekunden";
            }
        }
    }

    public String getTimeSinceEdited() {
        long diffSeconds = 0;
        Date edited = this.getEditedDate();
        diffSeconds = (new Date().getTime() - edited.getTime()) / 1000;

        if (diffSeconds > 864000) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd. MMM yyyy");
            return "am " + sdf.format(edited);

        } else if (diffSeconds >= 86400) {
            int value = (int) diffSeconds / 60 / 60 / 24;
            if(value == 1) {
                return "vor " + value + " Tag";
            } else {
                return "vor " + value + " Tagen";
            }
        } else if (diffSeconds >= 3600) {
            int value = (int) diffSeconds / 60 / 60;
            if(value == 1) {
                return "vor " + value + " Stunde";
            } else {
                return "vor " + value + " Stunden";
            }
        } else if (diffSeconds >= 60) {
            int value = (int) diffSeconds / 60;
            if(value == 1) {
                return "vor " + value + " Minute";
            } else {
                return "vor " + value + " Minuten";
            }
        } else {
            int value = (int) diffSeconds;
            if(value == 1) {
                return "vor " + value + " Sekunde";
            } else {
                return "vor " + value + " Sekunden";
            }
        }
    }

	public List<AssetTag> getTags() throws PortalException {
		return AssetEntryLocalServiceUtil.getEntry(Question.class.getName(), this.getQuestionID()).getTags();
	}

	public List<AssetCategory> getCategories() throws PortalException {
		return AssetEntryLocalServiceUtil.getEntry(Question.class.getName(), this.getQuestionID()).getCategories();
	}

	public String[] getTagNames() {
        try {
            return AssetEntryLocalServiceUtil.getEntry(Question.class.getName(), this.getQuestionID()).getTagNames();
        } catch (PortalException e) {
            log.error("Could not get asset entry for the specified question.");
            e.printStackTrace();
            return null;
        }
    }

	public long[] getCategoryIds() {
        try {
            return AssetEntryLocalServiceUtil.getEntry(Question.class.getName(), this.getQuestionID()).getCategoryIds();
        } catch (PortalException e) {
            log.error("Could not get asset entry for the specified question.");
            e.printStackTrace();
            return null;
        }
    }

    public String getTitle() throws PortalException {
        return AssetEntryLocalServiceUtil.getEntry(Question.class.getName(), this.getQuestionID()).getTitle();
    }

    public String getText() throws PortalException {
        return AssetEntryLocalServiceUtil.getEntry(Question.class.getName(), this.getQuestionID()).getDescription();
    }

	public int getViewCount() throws PortalException {
	    return AssetEntryLocalServiceUtil.getEntry(Question.class.getName(), this.getQuestionID()).getViewCount();
	}

	public double getRating() {
        RatingsStats ratingsStats = RatingsStatsLocalServiceUtil.getStats(Question.class.getName(),
                this.getQuestionID());
	    return ratingsStats.getTotalScore();
    }
}
