<%@ include file="init.jsp" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"/>
<h1><liferay-ui:message key="questions_and_answersde.sidate.questions_and_answers.portletQuestionsAndAnswersPortlet.caption"/></h1>

<portlet:renderURL var="newQuestionURL">
    <portlet:param name="mvcPath" value="/newQuestion.jsp"/>
</portlet:renderURL>

<p>;-)</p>

<jsp:useBean id="questions" class="java.util.ArrayList" scope="request"/>

<liferay-ui:search-container>
    <liferay-ui:search-container-results results="<%= questions %>"/>

    <liferay-ui:search-container-row className="de.sidate.questions_and_answers.model.Question" modelVar="question">
        <liferay-ui:search-container-column-text property="title" />

        <liferay-ui:search-container-column-text property="text" />
    </liferay-ui:search-container-row>

    <liferay-ui:search-iterator />
</liferay-ui:search-container>

<aui:button-row>
    <aui:button onClick="<%= newQuestionURL%>" value="New Question"></aui:button>
</aui:button-row>