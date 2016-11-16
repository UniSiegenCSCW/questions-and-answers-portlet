<%
    Answer answer = (Answer) pageContext.getAttribute("answer");
    User answerAuthor = UserLocalServiceUtil.getUser(answer.getUserId());
    List<Organization> answerAuthorOrganizations = answerAuthor.getOrganizations();
%>
<aui:row cssClass="qaContentContainer">
    <aui:col cssClass="qaAnswerRatingCol" span="1">
        <liferay-ui:ratings className="<%=Answer.class.getName()%>"
                            classPK="<%=answer.getAnswerID()%>" type="like" />
    </aui:col>
    <aui:col span="11">
        <aui:row>
            ${answer.text}
        </aui:row>
        <aui:row>

            <div class="qaAuthorbox">
                <div>beantwortet am <%=sdf.format(answer.getCreateDate())%></div>
                <div class="qaAuthorImage">
                    <img src="<%=answerAuthor.getPortraitURL(themeDisplay)%>"/>
                </div>
                <div class="qaAuthorInfo">
                    <%=answerAuthor.getFullName()%>
                    <c:if test="<%=answerAuthorOrganizations.size() > 0 %>">
                        <br/> <%=answerAuthorOrganizations.get(0).getName()%>
                    </c:if>
                </div>
            </div>
            <c:if test="<%=answer.getEditedDate() != null && answer.getEditedDate().after(answer.getCreateDate())%>">
                <%
                    User answerEditor = UserLocalServiceUtil.getUser(question.getUserId());
                    List<Organization> answerEditorOrganizations = answerEditor.getOrganizations();
                %>
                <div class="qaAuthorbox">
                    <div>editiert am <%=sdf.format(answer.getEditedDate())%></div>
                    <div class="qaAuthorImage">
                        <img src="<%=answerEditor.getPortraitURL(themeDisplay)%>"/>
                    </div>
                    <div class="qaAuthorInfo">
                        <%=answerEditor.getFullName()%>
                        <c:if test="<%=answerEditorOrganizations.size() > 0 %>">
                            <br/> <%=answerEditorOrganizations.get(0).getName()%>
                        </c:if>

                    </div>
                </div>
            </c:if>
        </aui:row>
        <aui:button-row cssClass="qaButtonRow">

            <c:if test="<%= QuestionsAndAnswersPortlet.answerPermissionContains(permissionChecker, answer, "EDIT") %>">
                <portlet:renderURL var="editAnswerURL">
                    <portlet:param name="mvcPath" value="/editAnswer.jsp"/>
                    <portlet:param name="backURL" value="<%= showQuestionsURL%>"/>
                    <portlet:param name="answerID" value="<%=String.valueOf(answer.getAnswerID())%>"/>
                </portlet:renderURL>
                <aui:button onClick="<%=editAnswerURL%>" value="Antwort bearbeiten"/>
            </c:if>

            <c:if test="<%= QuestionsAndAnswersPortlet.answerPermissionContains(permissionChecker, answer, "DELETE") %>">
                <portlet:actionURL name="deleteAnswer" var="deleteAnswerURL">
                    <portlet:param name="answerID" value="<%=String.valueOf(answer.getAnswerID())%>"/>
                    <portlet:param name="redirectURL" value="<%=showQuestionsURL%>"/>
                </portlet:actionURL>
                <aui:button onClick="<%=deleteAnswerURL%>" value="Antwort l&ouml;schen"/>
            </c:if>

            <c:if test="<%= QuestionsAndAnswersPortlet.questionPermissionContains(permissionChecker, question, "MARK_AS_CORRECT") %>">
                <portlet:actionURL name="setCorrectAnswer" var="setCorrectAnswerURL">
                    <portlet:param name="mvcPath" value="/showQuestion.jsp"/>
                    <portlet:param name="backURL" value="<%= backURL%>"/>
                    <portlet:param name="answerID" value="<%=String.valueOf(answer.getAnswerID())%>"/>
                    <portlet:param name="questionID" value="<%=String.valueOf(question.getQuestionID())%>"/>
                </portlet:actionURL>
                <aui:button onClick="<%=setCorrectAnswerURL%>" value="Antwort akzeptieren"/>
            </c:if>

        </aui:button-row>
        <aui:row cssClass='<%="qaDiscussionWrapper answerDiscussion_"+answer.getAnswerID()%>'>
            <a id="toggleAnswerComment_<%=answer.getAnswerID()%>_<%=identifier%>">neuen Kommentar hinzuf&uuml;gen</a>

            <aui:script>
               $("#toggleAnswerComment_<%=answer.getAnswerID()%>_<%=identifier%>").click(function () {
                $(".answerDiscussion_<%=answer.getAnswerID()%> .fieldset.add-comment").toggleClass("unhidden");
                });
            </aui:script>

            <portlet:actionURL name="invokeTaglibDiscussion" var="discussionURL" />

            <%
                String currentUrl = PortalUtil.getCurrentURL(request);
            %>

            <liferay-ui:discussion className="<%=Answer.class.getName()%>"
                                   classPK="<%=answer.getAnswerID()%>"
                                   formAction="<%=discussionURL%>"
                                   formName='<%="answerForm_"+answer.getAnswerID()%>'
                                   ratingsEnabled="<%=false%>"
                                   redirect="<%=currentUrl%>"
                                   userId="<%=themeDisplay.getUserId()%>" />

        </aui:row>
    </aui:col>
</aui:row>
