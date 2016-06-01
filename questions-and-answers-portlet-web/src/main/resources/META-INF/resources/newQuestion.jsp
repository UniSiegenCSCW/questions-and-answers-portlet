<%@ include file="init.jsp" %>

<p>TEST</p>

<portlet:actionURL name="addCategory" var="addCategoryURL"/>

<aui:form action="<%= addCategoryURL %>" name="<portlet:namespace />fm">
    <aui:fieldset>
        <aui:input name="name"/>
        <aui:input name="color"/>
    </aui:fieldset>
    <aui:button type="submit"/>
</aui:form>