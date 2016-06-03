<%@ include file="init.jsp" %>

<portlet:renderURL var="viewURL">
    <portlet:param name="mvcPath" value="/view.jsp"/>
</portlet:renderURL>

<p>TEST</p>

<portlet:actionURL name="newQuestion" var="newQuestionURL"/>

<aui:form action="<%= newQuestionURL %>" name="<portlet:namespace />fm">
    <aui:fieldset>
        <aui:input name="title"/>
        <aui:input name="text"/>
    </aui:fieldset>
    <aui:button type="submit"/>
    <aui:button type="cancel" onClick="<%= viewURL %>"/>
</aui:form>