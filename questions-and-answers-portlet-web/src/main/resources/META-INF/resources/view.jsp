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
<%@ page import="com.liferay.portal.kernel.util.ListUtil" %>


<%@ include file="init.jsp" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"/>

<jsp:useBean id="questions" class="java.util.ArrayList" scope="request"/>
<jsp:useBean id="questionsSortedByRating" class="java.util.ArrayList" scope="request"/>

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

<aui:container cssClass="qaQuestionOverviewWrapper">
    <aui:button-row>
        <%--<aui:button cssClass="pull-right" onClick="<%= testURL%>" value="Go to testing view"></aui:button>--%>
        <aui:button cssClass="pull-left btn-primary" onClick="<%= newQuestionURL%>" value="Neue Frage stellen"></aui:button>
    </aui:button-row>
    <aui:row>
        <aui:col span="10">
            <liferay-ui:tabs names="Neue Fragen,Beste Fragen" refresh="false" tabsValues="Neue Fragen,Beste Fragen">
                <liferay-ui:section>
                    <%-- Questions will be sorted by date by default --%>
                    <% Collections.reverse(questions); %>

                    <aui:container cssClass="qaQuestionsOverviewContainer">
                        <liferay-ui:search-container delta="10" deltaConfigurable="false" emptyResultsMessage="Es gibt noch keine Fragen" total="<%=questions.size()%>">
                            <liferay-ui:search-container-results results="<%= ListUtil.subList(questions, searchContainer.getStart(), searchContainer.getEnd()) %>" />
                            <liferay-ui:search-container-row modelVar="question"
                                                             className="org.sidate.qanda.model.Question">
                                <%@ include file="_question.jsp" %>
                            </liferay-ui:search-container-row>
                            <liferay-ui:search-iterator />
                        </liferay-ui:search-container>
                    </aui:container>
                </liferay-ui:section>

                <liferay-ui:section>
                    <aui:container cssClass="qaQuestionsOverviewContainer">
                        <liferay-ui:search-container delta="10" deltaConfigurable="false" emptyResultsMessage="Es gibt noch keine Fragen" total="<%=questionsSortedByRating.size()%>">
                            <liferay-ui:search-container-results results="<%= ListUtil.subList(questionsSortedByRating, searchContainer.getStart(), searchContainer.getEnd()) %>" />
                            <liferay-ui:search-container-row modelVar="question"
                                                             className="org.sidate.qanda.model.Question">
                                <%@ include file="_question.jsp" %>
                            </liferay-ui:search-container-row>
                            <liferay-ui:search-iterator />
                        </liferay-ui:search-container>
                    </aui:container>
                </liferay-ui:section>
            </liferay-ui:tabs>
        </aui:col>
        <aui:col span="2">
            <c:if test="${tags.size() > 0}">
                <aui:row>
                    <h5>Tags</h5>
                    <div class="qaTagOverview">
                        <c:forEach var="tag" items="${tags}">
                            <portlet:renderURL var="tagsURL">
                                <portlet:param name="mvcPath" value="/tags.jsp"/>
                                <portlet:param name="tag" value="${tag.name}"/>
                                <portlet:param name="backURL" value="<%= mainViewURL%>"/>
                            </portlet:renderURL>
                            <%
                                AssetTag tag = (AssetTag) pageContext.getAttribute("tag");
                                int code = tag.getName().hashCode();
                                // Modulo fix so that it works for negative integers
                                int index = (code % tagColors.length + tagColors.length) % tagColors.length;
                                String color = tagColors[index];
                            %>
                            <span style="background: <%= color %>">
                                <a href="<%= tagsURL %>">${tag.name}</a>
                            </span>
                        </c:forEach>
                    </div>
                </aui:row>
            </c:if>
            <c:if test="${categories.size() > 0}">
                <aui:row>
                    <h5>Kategorien</h5>
                    <div class="qaTagOverview">
                        <c:forEach var="category" items="${categories}">
                            <portlet:renderURL var="categoryURL">
                                <portlet:param name="mvcPath" value="/categories.jsp"/>
                                <portlet:param name="category" value="${category.name}"/>
                                <portlet:param name="backURL" value="<%= mainViewURL%>"/>
                            </portlet:renderURL>
                            <%
                                AssetCategory category = (AssetCategory) pageContext.getAttribute("category");
                                int code = category.getName().hashCode();
                                // Modulo fix so that it works for negative integers
                                int index = (code % tagColors.length + tagColors.length) % tagColors.length;
                                String color = tagColors[index];
                            %>
                            <span style="background: <%= color %>">
                                <a href="<%= categoryURL %>">${category.name}</a>
                            </span>
                        </c:forEach>
                    </div>
                </aui:row>
            </c:if>
        </aui:col>
    </aui:row>
</aui:container>
