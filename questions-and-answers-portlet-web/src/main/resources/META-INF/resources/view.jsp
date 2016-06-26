<%@ page import="de.sidate.questions_and_answers.model.Question" %>
<%@ include file="init.jsp" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"/>

<jsp:useBean id="questions" class="java.util.ArrayList" scope="request"/>

<% String tabNames = "Neue Fragen,Beste Fragen"; %>
<liferay-ui:tabs
        names="<%= tabNames %>"
/>

<aui:container>
    <c:forEach var="question" items="${questions}">
        <aui:container cssClass="qaQuestionEntryContainer">
            <aui:row>
                <aui:col span="8">
                    <aui:row>
                        <aui:col>
                            <h5>${question.title}</h5>
                        </aui:col>
                    </aui:row>
                </aui:col>
                <aui:col span="4">

                    <div class="qaRatingBox">
                        <div class="qaCounterValue">10</div>
                        <div class="qaCounterLabel">Bewertungen</div>
                    </div>
                    <div class="qaAnswersBox
                        <c:if test="${question.correctAnswerId != 0}">
                            answered
                        </c:if>
                        ">
                        <div class="qaCounterValue">3</div>
                        <div class="qaCounterLabel">Antworten</div>
                    </div>
                </aui:col>
            </aui:row>
            <aui:row>
                <aui:col>
                    <div class="qaTagContainer">
                        <strong>Tags:</strong>
                        <ul class="qaTags">
                            <li>toll</li>
                            <li>super</li>
                            <li>perfekt</li>
                        </ul>
                    </div>
                    <div class="qaCategoryContainer">
                        <strong>Kategorien:</strong>
                        <ul class="qaCategories">
                            <li>toll</li>
                            <li>super</li>
                            <li>perfekt</li>
                        </ul>
                    </div>
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
