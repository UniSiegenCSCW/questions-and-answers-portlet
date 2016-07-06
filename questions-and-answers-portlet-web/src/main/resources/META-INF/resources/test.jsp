<%@ include file="init.jsp" %>

<portlet:actionURL name="testAnswer" var="testAnswerURL"/>
<aui:button onClick="<%= testAnswerURL%>" value="Test new Answer"></aui:button>

<portlet:actionURL name="testNewQuestion" var="testNewQuestionURL"/>
<aui:button onClick="<%= testNewQuestionURL%>" value="Test new Question"></aui:button>

<portlet:actionURL name="testSetCorrectAnswer" var="testSetCorrectAnswerURL"/>
<aui:button onClick="<%= testSetCorrectAnswerURL%>" value="Accept the first answer of first question"></aui:button>

<portlet:actionURL name="testDisplayCorrectAnswer" var="testDisplayCorrectAnswerURL"/>
<aui:button onClick="<%= testDisplayCorrectAnswerURL%>" value="Display the correct answer of first question"></aui:button>

<portlet:actionURL name="testDeleteAnswer" var="testDeleteAnswerURL"/>
<aui:button onClick="<%= testDeleteAnswerURL%>" value="Delete answer from first question"></aui:button>

<portlet:actionURL name="testDeleteQuestion" var="testDeleteQuestionURL"/>
<aui:button onClick="<%= testDeleteQuestionURL%>" value="Delete the first question"></aui:button>

<portlet:actionURL name="testEditQuestion" var="testEditQuestionURL"/>
<aui:button onClick="<%= testEditQuestionURL%>" value="Edit first question"></aui:button>

<portlet:actionURL name="testEditAnswer" var="testEditAnswerURL"/>
<aui:button onClick="<%= testEditAnswerURL%>" value="Edit the first answer of first question"></aui:button>

<portlet:actionURL name="testDisplayAssetCount" var="testDisplayAssetsURL"/>
<aui:button onClick="<%= testDisplayAssetsURL%>" value="Display the asset count"></aui:button>

<portlet:actionURL name="testDisplayAssets" var="testDisplayAssetsURL"/>
<aui:button onClick="<%= testDisplayAssetsURL%>" value="Display the assets"></aui:button>

<portlet:actionURL name="testDeleteAssets" var="testDeleteAssetsURL"/>
<aui:button onClick="<%= testDeleteAssetsURL%>" value="Delete all assets"></aui:button>