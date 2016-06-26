<%@ include file="init.jsp" %>

<portlet:actionURL name="testAnswer" var="testAnswerURL"/>
<aui:button onClick="<%= testAnswerURL%>" value="Test new Answer"></aui:button>

<portlet:actionURL name="testCorrectAnswer" var="testAcceptAnswerURL"/>
<aui:button onClick="<%= testAcceptAnswerURL%>" value="Display correct Answer (in the Terminal)"></aui:button>

<portlet:actionURL name="testDeleteAnswer" var="testDeleteAnswerURL"/>
<aui:button onClick="<%= testDeleteAnswerURL%>" value="Delete answer from first question"></aui:button>

<portlet:actionURL name="testEditQuestion" var="testEditQuestionURL"/>
<aui:button onClick="<%= testEditQuestionURL%>" value="Edit first question"></aui:button>

<portlet:actionURL name="testEditAnswer" var="testEditAnswerURL"/>
<aui:button onClick="<%= testEditAnswerURL%>" value="Edit the first answer of first question"></aui:button>

<portlet:actionURL name="testDisplayAssets" var="testDisplayAssetsURL"/>
<aui:button onClick="<%= testDisplayAssetsURL%>" value="Display the asset count"></aui:button>

