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

package org.sidate.questions_and_answers.model.impl;

import aQute.bnd.annotation.ProviderType;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The extended model implementation for the Question service. Represents a row in the &quot;SIDATE_Question&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * Helper methods and all application logic should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link de.sidate.questions_and_answers.model.Question} interface.
 * </p>
 *
 * @author Brian Wing Shun Chan
 */
@ProviderType
public class QuestionImpl extends QuestionBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. All methods that expect a question model instance should use the {@link de.sidate.questions_and_answers.model.Question} interface instead.
	 */
	public QuestionImpl() {
	}

	public String getTimeDifferenceString() {
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
}