<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="org.sidate.questions_and_answers.model.Question" %>
<%@ page import="org.sidate.questions_and_answers.service.QuestionLocalServiceUtil" %>
<%@ page import="com.liferay.portal.kernel.util.StringUtil" %>
<%@ page import="com.liferay.portal.kernel.util.UnicodeFormatter" %>
<%@ include file="init.jsp" %>

<%
    String backURL = ParamUtil.getString(renderRequest, "backURL");
    long questionId = ParamUtil.getLong(renderRequest, "questionID");

    Question question = null;
    String pageTitle = "Neue Frage stellen";
    String questionText = "";

    if (questionId > 0) {
        question = QuestionLocalServiceUtil.getQuestion(questionId);
        pageTitle = "Frage bearbeiten";
        questionText = UnicodeFormatter.toString(question.getText());
    }
%>

<liferay-ui:header
        backURL="<%= backURL %>"
        title='<%= pageTitle%>'
/>
<portlet:actionURL name="newQuestion" var="editQuestionURL"/>

<c:if test="<%=question != null%>">
    <portlet:actionURL name="editQuestion" var="editQuestionURL"/>
</c:if>

<liferay-ui:asset-categories-error />
<liferay-ui:asset-tags-error />

<aui:form action="<%= editQuestionURL %>" name="<portlet:namespace />editQuestion">
    <aui:model-context bean="<%=question%>" model="<%=Question.class%>"/>
    <aui:input name="questionID" type="hidden" />
        <aui:fieldset>
            <aui:input label="Titel der Frage" name="title"/>
            <br/>
                <liferay-ui:input-editor name="text" initMethod="initEditor" />
                <aui:script>
                    function <portlet:namespace />initEditor() {
                        return "<%= questionText %>";
                    }
                </aui:script>
        </aui:fieldset>
        <aui:fieldset>
            <div>
                <strong>Kategorien</strong>
                <c:choose>
                    <c:when test="<%=question != null%>">
                        <liferay-ui:asset-categories-selector curCategoryIds="<%=StringUtil.merge(question.getCategoryIds(),\",\")%>"/>
                    </c:when>
                    <c:otherwise>
                        <liferay-ui:asset-categories-selector />
                    </c:otherwise>
                </c:choose>

            </div><br/>
            <div>
                <strong>Tags</strong>
                <c:choose>
                    <c:when test="<%=question != null%>">
                        <liferay-ui:asset-tags-selector curTags="<%=StringUtil.merge(question.getTagNames(),\",\")%>"/>
                    </c:when>
                    <c:otherwise>
                        <liferay-ui:asset-tags-selector />
                    </c:otherwise>
                </c:choose>
            </div>
        </aui:fieldset>
    <aui:button type="submit"/>
    <aui:button type="cancel" onClick="<%= backURL %>"/>
</aui:form>