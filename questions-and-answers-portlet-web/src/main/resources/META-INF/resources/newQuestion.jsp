<%@ include file="init.jsp" %>

<portlet:renderURL var="viewURL">
    <portlet:param name="mvcPath" value="/view.jsp"/>
</portlet:renderURL>

<h5>Neue Frage stellen</h5>

<portlet:actionURL name="newQuestion" var="newQuestionURL"/>
<liferay-ui:asset-categories-error />
<liferay-ui:asset-tags-error />

<aui:form action="<%= newQuestionURL %>" name="<portlet:namespace />fm">
    <aui:fieldset-group markupView="lexicon">
        <aui:fieldset>
            <aui:input label="Titel der Frage" name="title"/>
            <br/>
            <liferay-ui:input-editor name="text"/>
        </aui:fieldset>
            <aui:fieldset>
                <div>
                    <strong>Kategorien</strong>
                    <liferay-ui:asset-categories-selector />
                </div>
                <div>
                    <strong>Tags</strong>
                    <liferay-ui:asset-tags-selector />
                </div>
            </aui:fieldset>
    </aui:fieldset-group>
    <aui:button type="submit"/>
    <aui:button type="cancel" onClick="<%= viewURL %>"/>
</aui:form>