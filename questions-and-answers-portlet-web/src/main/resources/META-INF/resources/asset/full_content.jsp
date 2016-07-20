<%@ page import="org.sidate.questions_and_answers.model.Question" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %><%--
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
--%>

<%@ include file="../init.jsp" %>

<%
    Question question = (Question)request.getAttribute("QUESTION");
    int status = ParamUtil.getInteger(request, "status", WorkflowConstants.STATUS_ANY);
%>

<%--<aui:a href='<%= themeDisplay.getPathMain() + "/bookmarks/open_entry?entryId=" + question.getEntryId() + ((status != WorkflowConstants.STATUS_ANY) ? "&status=" + status : StringPool.BLANK) %>' target="_blank"><%= HtmlUtil.escape(question.getName()) %> (<%= HtmlUtil.escape(question.getUrl()) %>)</aui:a>--%>

<p class="asset-description"><%= HtmlUtil.escape(question.getTitle()) %></p>

<liferay-ui:custom-attributes-available className="<%= Question.class.getName() %>">
    <liferay-ui:custom-attribute-list
            className="<%= Question.class.getName() %>"
            classPK="<%= (question != null) ? question.getQuestionID() : 0 %>"
            editable="<%= false %>"
            label="<%= true %>"
    />
</liferay-ui:custom-attributes-available>