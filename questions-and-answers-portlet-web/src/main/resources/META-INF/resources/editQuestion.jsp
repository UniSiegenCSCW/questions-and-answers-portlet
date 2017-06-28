<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="org.sidate.qanda.model.Question" %>
<%@ page import="org.sidate.qanda.service.QuestionLocalServiceUtil" %>
<%@ page import="com.liferay.portal.kernel.util.StringUtil" %>
<%@ page import="com.liferay.portal.kernel.util.UnicodeFormatter" %>
<%@ page import="com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil" %>
<%@ page import="com.liferay.asset.kernel.model.AssetEntry" %>
<%@ page import="java.util.List" %>
<%@ page import="com.liferay.asset.kernel.model.AssetCategory" %>
<%@ page import="java.util.ArrayList" %>
<%@ include file="init.jsp" %>

<head>
    <meat charset = "utf-8" />
</head>

<%
    String backURL = ParamUtil.getString(renderRequest, "backURL");
    long questionId = ParamUtil.getLong(renderRequest, "questionID");

    String isQuestionToProcedure = renderRequest.getParameter("oper");
    String procedureId = renderRequest.getParameter("prid");



    Question question = null;
    String pageTitle = "Neue Frage stellen";
    String questionText = "";

    long[] categoryIds = new long[0];

    if(isQuestionToProcedure != null && isQuestionToProcedure.equals("crqp")){
        //pageTitle = "Neue Frage zur Maßnahme stellen";
        categoryIds = AssetEntryLocalServiceUtil.getAssetEntry(Long.valueOf(procedureId)).getCategoryIds();
        pageTitle = "Neue Frage zur Ma\u00DFnahme " + AssetEntryLocalServiceUtil.getAssetEntry(Long.valueOf(procedureId)).getTitle(locale) + " stellen.";
    }

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
<portlet:actionURL name="newQuestion" var="editQuestionURL">
        <portlet:param name="backURL" value="<%= backURL %>" />
</portlet:actionURL>


<c:if test="<%=question != null%>">
    <portlet:renderURL var="redirectURL">
        <portlet:param name="mvcPath" value="/showQuestion.jsp"/>
        <portlet:param name="backURL" value="<%= backURL%>"/>
        <portlet:param name="questionID" value="<%=String.valueOf(question.getQuestionID())%>"/>
    </portlet:renderURL>

    <portlet:actionURL name="editQuestion" var="editQuestionURL">
        <portlet:param name="backURL" value="<%= backURL %>" />
        <portlet:param name="redirectURL" value="<%= redirectURL %>" />
    </portlet:actionURL>
</c:if>

<liferay-ui:asset-categories-error />
<liferay-ui:asset-tags-error />

<aui:form action="<%= editQuestionURL %>" name="<portlet:namespace />editQuestion">
    <aui:model-context bean="<%=question%>" model="<%=Question.class%>"/>
    <aui:input name="questionID" type="hidden" />
        <aui:fieldset>
            <aui:input label="Titel der Frage" name="title" type="text" required="true" maxlength="40"/>
            <br/>
                <liferay-ui:input-editor name="text" initMethod="initEditor"/>
                <aui:script>
                    function <portlet:namespace />initEditor() {
                        return "<%= questionText %>";
                    }
                </aui:script>
            <aui:input name="isQuestionToProcedure" type="hidden" value="<%=isQuestionToProcedure%>" />
            <aui:input name="procedureId" type="hidden" value="<%=procedureId%>" />
        </aui:fieldset>
    <aui:fieldset>
        <div>
            <strong>Kategorien</strong>
            <c:choose>
                <c:when test="<%=question != null%>">
                    <liferay-ui:asset-categories-selector curCategoryIds="<%=StringUtil.merge(question.getCategoryIds(),\",\")%>"/>
                </c:when>
                <c:when test="<%=isQuestionToProcedure != null && isQuestionToProcedure.equals("crqp")%>">
                    <liferay-ui:asset-categories-selector curCategoryIds="<%=StringUtil.merge(categoryIds,\",\")%>"/>
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
                    <liferay-ui:asset-tags-selector id="qanda" curTags="<%=StringUtil.merge(question.getTagNames(),\",\")%>"/>
                </c:when>
                <c:otherwise>
                    <liferay-ui:asset-tags-selector id="qanda" />
                </c:otherwise>
            </c:choose>
            <script>
                // FIXME, this is a hotfix to resolve issue #16
                var elem = $("input[id=qandaassetTagNames]")
                elem.on("keyup", () => {
                    elem.val(elem.val().toLowerCase())
                })
            </script>
        </div>
    </aui:fieldset>
    <aui:button type="submit" />
    <aui:button type="cancel" onClick="<%= backURL %>"/>
</aui:form>
