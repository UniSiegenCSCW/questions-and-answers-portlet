<%@ page import="de.sidate.questions_and_answers.model.Question" %>
<%@ page import="com.liferay.asset.kernel.model.AssetEntry" %>
<%@ page import="com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="com.liferay.asset.kernel.model.AssetTag" %>
<%@ page import="com.liferay.asset.kernel.model.AssetCategory" %>
<%@ page import="de.sidate.questions_and_answers.model.Answer" %>
<%@ page import="de.sidate.questions_and_answers.service.AnswerLocalServiceUtil" %>
<%@ page import="com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil" %>
<%@ page import="com.liferay.ratings.kernel.model.RatingsStats" %>
<%@ include file="init.jsp" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"/>

<jsp:useBean id="questions" class="java.util.ArrayList" scope="request"/>

<% String tabNames = "Neue Fragen,Beste Fragen"; %>
<liferay-ui:tabs
        names="<%= tabNames %>"
/>


<aui:container>
    <c:forEach var="question" items="${questions}">
        <%
            Question question = (Question)pageContext.getAttribute("question");
            AssetEntry asset = AssetEntryLocalServiceUtil.getEntry(Question.class.getName(), question.getQuestionID());
            RatingsStats ratingsStats = RatingsStatsLocalServiceUtil.getStats(Question.class.getName(), question.getQuestionID());
            List<AssetTag> tags = asset.getTags();
            List<AssetCategory> categories = asset.getCategories();
            List<Answer> answers = AnswerLocalServiceUtil.getAnswersForQuestion(question.getQuestionID());
        %>
        <aui:container cssClass="qaQuestionEntryContainer">
            <aui:row>
                <aui:col span="9">
                    <aui:row>
                        <aui:col>
                            <portlet:renderURL var="mainViewURL">
                                <portlet:param name="mvcPath" value="/view.jsp"/>
                            </portlet:renderURL>
                            <portlet:renderURL var="showQuestionURL">
                                <portlet:param name="mvcPath" value="/showQuestion.jsp"/>
                                <portlet:param name="backURL" value="<%= mainViewURL%>"/>
                                <portlet:param name="questionID" value="${question.questionID}"/>
                            </portlet:renderURL>

                            <h5><a href="<%= showQuestionURL%>">${question.title}</a></h5>
                        </aui:col>
                    </aui:row>
                </aui:col>
                <aui:col span="3">

                    <div class="qaRatingBox">
                        <div class="qaCounterValue">
                            <%=(int) ratingsStats.getTotalScore()%>
                        </div>
                        <div class="qaCounterLabel">Bewertungen</div>
                    </div>
                    <div class="qaAnswersBox
                        <c:if test="${question.correctAnswerId != 0}">
                            answered
                        </c:if>
                        ">
                        <div class="qaCounterValue"><%=answers.size()%></div>
                        <div class="qaCounterLabel">Antworten</div>
                    </div>
                </aui:col>
            </aui:row>
            <aui:row>
                <aui:col>
                    <c:if test="<%=tags.size() > 0 %>">
                        <div class="qaTagContainer">
                            <strong>Tags:</strong>
                            <ul class="qaTags">
                                <c:forEach items="<%= tags%>" var="tag">
                                    <li>${tag.name}</li>
                                </c:forEach>
                            </ul>
                        </div>
                    </c:if>
                    <c:if test="<%=categories.size() > 0 %>">
                        <div class="qaCategoryContainer">
                            <strong>Kategorien:</strong>
                            <ul class="qaCategories">
                                <c:forEach items="<%= categories%>" var="category">
                                    <li>${category.name}</li>
                                </c:forEach>
                            </ul>
                        </div>
                    </c:if>
                </aui:col>
            </aui:row>
            <aui:row>
                <aui:col>
                    <span class="qaDateTime">Frage ${question.getTimeDifferenceString()} gestellt</span>
                </aui:col>
            </aui:row>
        </aui:container>
    </c:forEach>
</aui:container>

<aui:button-row>
    <portlet:renderURL var="newQuestionURL"><portlet:param name="mvcPath" value="/newQuestion.jsp"/></portlet:renderURL>
    <aui:button onClick="<%= newQuestionURL%>" value="New Question"></aui:button>

    <portlet:renderURL var="testURL"><portlet:param name="mvcPath" value="/test.jsp"/></portlet:renderURL>
    <aui:button onClick="<%= testURL%>" value="Go to testing view"></aui:button>
</aui:button-row>
