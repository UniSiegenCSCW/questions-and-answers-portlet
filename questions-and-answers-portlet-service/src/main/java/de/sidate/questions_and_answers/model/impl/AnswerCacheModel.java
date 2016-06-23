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

package de.sidate.questions_and_answers.model.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.util.HashUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;

import de.sidate.questions_and_answers.model.Answer;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing Answer in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @see Answer
 * @generated
 */
@ProviderType
public class AnswerCacheModel implements CacheModel<Answer>, Externalizable {
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof AnswerCacheModel)) {
			return false;
		}

		AnswerCacheModel answerCacheModel = (AnswerCacheModel)obj;

		if (answerID == answerCacheModel.answerID) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, answerID);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(21);

		sb.append("{uuid=");
		sb.append(uuid);
		sb.append(", answerID=");
		sb.append(answerID);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", userName=");
		sb.append(userName);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", text=");
		sb.append(text);
		sb.append(", questionId=");
		sb.append(questionId);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public Answer toEntityModel() {
		AnswerImpl answerImpl = new AnswerImpl();

		if (uuid == null) {
			answerImpl.setUuid(StringPool.BLANK);
		}
		else {
			answerImpl.setUuid(uuid);
		}

		answerImpl.setAnswerID(answerID);
		answerImpl.setGroupId(groupId);
		answerImpl.setCompanyId(companyId);
		answerImpl.setUserId(userId);

		if (userName == null) {
			answerImpl.setUserName(StringPool.BLANK);
		}
		else {
			answerImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			answerImpl.setCreateDate(null);
		}
		else {
			answerImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			answerImpl.setModifiedDate(null);
		}
		else {
			answerImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (text == null) {
			answerImpl.setText(StringPool.BLANK);
		}
		else {
			answerImpl.setText(text);
		}

		answerImpl.setQuestionId(questionId);

		answerImpl.resetOriginalValues();

		return answerImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		uuid = objectInput.readUTF();

		answerID = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		text = objectInput.readUTF();

		questionId = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput)
		throws IOException {
		if (uuid == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(uuid);
		}

		objectOutput.writeLong(answerID);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		if (text == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(text);
		}

		objectOutput.writeLong(questionId);
	}

	public String uuid;
	public long answerID;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String text;
	public long questionId;
}