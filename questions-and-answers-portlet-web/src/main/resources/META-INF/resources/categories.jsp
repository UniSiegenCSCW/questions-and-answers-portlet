<%@ page import="org.sidate.qanda.model.Question" %>
<%@ page import="com.liferay.asset.kernel.model.AssetEntry" %>
<%@ page import="com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="com.liferay.asset.kernel.model.AssetTag" %>
<%@ page import="com.liferay.asset.kernel.model.AssetCategory" %>
<%@ page import="org.sidate.qanda.model.Answer" %>
<%@ page import="org.sidate.qanda.service.AnswerLocalServiceUtil" %>
<%@ page import="com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil" %>
<%@ page import="com.liferay.ratings.kernel.model.RatingsStats" %>
<%@ page import="com.liferay.portal.kernel.model.User" %>
<%@ page import="com.liferay.portal.kernel.service.UserLocalServiceUtil" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.liferay.portal.kernel.model.Organization" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.sidate.qanda.service.QuestionLocalServiceUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ListUtil" %>
<%@ include file="init.jsp" %>

<jsp:useBean id="questionsFilteredByCategory" class="java.util.ArrayList" scope="request"/>


<%
    String backURL = ParamUtil.getString(renderRequest, "backURL");
    String pageCategory = ParamUtil.getString(renderRequest, "category");
%>

<portlet:renderURL var="mainViewURL">
    <portlet:param name="mvcPath" value="/view.jsp"/>
</portlet:renderURL>

<portlet:renderURL var="newQuestionURL">
    <portlet:param name="mvcPath" value="/editQuestion.jsp"/>
    <portlet:param name="backURL" value="<%= mainViewURL%>"/>
</portlet:renderURL>

<portlet:renderURL var="testURL">
    <portlet:param name="mvcPath" value="/test.jsp"/>
</portlet:renderURL>

<liferay-ui:header
        backURL="<%= backURL %>"
        title='<%= pageCategory %>'
/>

<aui:container cssClass="qaQuestionOverviewWrapper">

    <%-- Questions will be sorted by date by default --%>
    <% Collections.reverse(questionsFilteredByCategory); %>

    <aui:container cssClass="qaQuestionsOverviewContainer">
        <liferay-ui:search-container delta="10" deltaConfigurable="false" emptyResultsMessage="Es gibt noch keine Fragen" total="<%=questionsFilteredByCategory.size()%>">
            <liferay-ui:search-container-results results="<%= ListUtil.subList(questionsFilteredByCategory, searchContainer.getStart(), searchContainer.getEnd()) %>" />
            <liferay-ui:search-container-row modelVar="question"
                                             className="org.sidate.qanda.model.Question">
                <%@ include file="_question.jsp" %>
            </liferay-ui:search-container-row>
            <liferay-ui:search-iterator />
        </liferay-ui:search-container>
    </aui:container>
</aui:container>