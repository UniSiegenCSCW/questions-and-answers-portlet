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
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.ratings.kernel.model.RatingsStats;
import com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil;
import org.sidate.qanda.exception.CorrectAnswerNotSetException;
import org.sidate.qanda.model.Answer;
import org.sidate.qanda.model.Question;
import org.sidate.qanda.service.AnswerLocalServiceUtil;
import org.sidate.qanda.service.QuestionLocalServiceUtil;
import org.sidate.qanda.model.util.TimeDiffFormatter;

import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.stream.Collectors.toList;

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
		Date created = this.getCreateDate();
		return TimeDiffFormatter.format(created);
	}

	public String getTimeSinceEdited() {
		Date edited = this.getEditedDate();
		return TimeDiffFormatter.format(edited);
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

	public void setCorrectAnswer(long answerId) {
		QuestionLocalServiceUtil.setCorrectAnswer(answerId, this.getQuestionID());
	}

	public void unsetCorrectAnswer() {
		QuestionLocalServiceUtil.unsetCorrectAnswer(this.getQuestionID());
	}

	public Answer getCorrectAnswer() throws PortalException {
		if (this.getIsAnswered()) {
			return AnswerLocalServiceUtil.getAnswer(this.getCorrectAnswerId());
		} else {
			log.error("Correct answer has not been set for question " + this.getQuestionID());
			throw new CorrectAnswerNotSetException();
		}
	}

	public List<Answer> getAnswersSortedByRating() {

		List<Answer> answers = AnswerLocalServiceUtil.getAnswersForQuestion(this.getQuestionID());

		Comparator<Answer> byRating = (answerOne, answerTwo) -> Double.compare(answerTwo.getRating(),
				answerOne.getRating());

		return answers.stream()
				.sorted(byRating)
				.collect(toList());
	}

	public void increaseViewCounter(long watcherId) {
		try {
			AssetEntryLocalServiceUtil.incrementViewCounter(watcherId, Question.class.getName(), this.getQuestionID());
		} catch (PortalException e) {
			log.error("Could not increment viewCount for question " + this.getQuestionID() + " with watching user "
					+ watcherId);
		}
	}
}
