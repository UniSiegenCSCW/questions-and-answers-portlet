<%@ include file="init.jsp" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"/>
<h1><liferay-ui:message key="questions_and_answersde.sidate.questions_and_answers.portletQuestionsAndAnswersPortlet.caption"/></h1>

<ul class="questions question list-group">
    <li class="list-group-item">
        <i class="isAnswered glyphicon glyphicon-ok"></i>
        <div class="questionText">Das ist eine tolle Frage!</div>
        Kategorien:
        <ul class="categories">
            <li>ISMS</li>
            <li>Blub</li>
        </ul>
        Tags:
        <ul class="tags">
            <li>ISMS</li>
            <li>Blub</li>
        </ul>

    </li>
</ul>

<portlet:actionURL name="addCategory" var="addCategoryURL"/>

<aui:form action="<%= addCategoryURL %>" name="<portlet:namespace />fm">
    <aui:fieldset>
        <aui:input name="name"/>
        <aui:input name="color"/>
    </aui:fieldset>
    <aui:button type="submit"/>
</aui:form>