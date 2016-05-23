<%@ include file="init.jsp" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"/>
<h1><liferay-ui:message key="questions_and_answersde.sidate.questions_and_answers.portletQuestionsAndAnswersPortlet.caption"/></h1>

<ul class="questions question list-group">
    <li class="list-group-item">
        <i class="isAnswered glyphicon glyphicon-ok"></i>
        <h2 class="questionText">Das ist eine tolle Frage!</h2>
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