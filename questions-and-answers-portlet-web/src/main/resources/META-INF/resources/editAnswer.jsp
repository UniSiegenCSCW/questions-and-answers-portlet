<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="org.sidate.qanda.model.Answer" %>
<%@ page import="org.sidate.qanda.service.AnswerLocalServiceUtil" %>
<%@ page import="com.liferay.portal.kernel.util.UnicodeFormatter" %>
<%@ include file="init.jsp" %>

<%
    String backURL = ParamUtil.getString(renderRequest, "backURL");
    long answerId = ParamUtil.getLong(renderRequest, "answerID");

    Answer answer = null;
    String pageTitle = "Neue Frage stellen";
    String answerText = "";

    if (answerId > 0) {
        answer = AnswerLocalServiceUtil.getAnswer(answerId);
        pageTitle = "Frage bearbeiten";
        answerText = UnicodeFormatter.toString(answer.getText());
    }
%>

<liferay-ui:header
        backURL="<%= backURL %>"
        title='Antwort bearbeiten'
/>
<portlet:actionURL name="editAnswer" var="editAnswerURL">
    <portlet:param name="redirectURL" value="<%=backURL%>"/>
</portlet:actionURL>

<aui:form action="<%= editAnswerURL %>" name="<portlet:namespace />editAnswer">
    <aui:model-context bean="<%=answer%>" model="<%=Answer.class%>"/>
    <aui:input name="answerID" type="hidden" />
    <aui:fieldset>
        <liferay-ui:input-editor name="text" initMethod="initEditor" />
        <aui:script>
            function <portlet:namespace />initEditor() {
            return "<%= answerText %>";
            }
        </aui:script>
    </aui:fieldset>
    <aui:button type="submit"/>
    <aui:button type="cancel" onClick="<%= backURL %>"/>
</aui:form>