<%@ page import="org.sidate.qanda.model.Question" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="org.sidate.qanda.service.QuestionLocalServiceUtil" %>
<%@ page import="org.sidate.qanda.model.Answer" %>
<%@ page import="org.sidate.qanda.service.AnswerLocalServiceUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil" %>
<%@ page import="com.liferay.asset.kernel.model.AssetEntry" %>
<%@ page import="com.liferay.asset.kernel.model.AssetTag" %>
<%@ page import="com.liferay.asset.kernel.model.AssetCategory" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.liferay.portal.kernel.service.UserLocalServiceUtil" %>
<%@ page import="com.liferay.portal.kernel.model.User" %>
<%@ page import="com.liferay.portal.kernel.util.PortalUtil" %>
<%@ page import="com.liferay.portal.kernel.model.Organization" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.sidate.qanda.portlet.QuestionsAndAnswersPortlet" %>
<%@ include file="init.jsp" %>

<%
    long questionID = ParamUtil.getLong(renderRequest, "questionID");
    String backURL = ParamUtil.getString(renderRequest, "backURL");
    Question question = QuestionLocalServiceUtil.getQuestion(questionID);
    List<AssetTag> tags = question.getTags();
    List<AssetCategory> categories = question.getCategories();
    ArrayList<Answer> answersSortedByDate = new ArrayList<>(AnswerLocalServiceUtil.getAnswersForQuestion(questionID));
    ArrayList<Answer> answersSortedByRating = new ArrayList<>(question.getAnswersSortedByRating());
    question.increaseViewCounter(themeDisplay.getUserId());

    SimpleDateFormat sdf = new SimpleDateFormat("dd. MMM yyyy 'um' hh:mm", Locale.GERMAN);

    User author = UserLocalServiceUtil.getUser(question.getUserId());
    List<Organization> authorOrganisations = author.getOrganizations();

%>

<portlet:renderURL var="viewURL">
    <portlet:param name="mvcPath" value="/view.jsp"/>
</portlet:renderURL>

<portlet:renderURL var="showQuestionsURL">
    <portlet:param name="mvcPath" value="/showQuestion.jsp"/>
    <portlet:param name="backURL" value="<%= backURL%>"/>
    <portlet:param name="questionID" value="<%=String.valueOf(question.getQuestionID())%>"/>
</portlet:renderURL>

<liferay-ui:header
        backURL="<%= backURL %>"
        title='<%=question.getTitle() %>'
/>

<aui:container cssClass="qaQuestionWrapper">
    <c:if test="<%= question.getIsQuestionToProcedure()%>">
        <aui:container cssClass="procedureLinkWrapper">
            <a href="<%=AssetEntryLocalServiceUtil.getAssetEntry(question.getProcedureId()).getAssetRenderer().getURLViewInContext(liferayPortletRequest, liferayPortletResponse, "")%>"> Dies Frage wurde zur Ma&szlig;nahme <%= AssetEntryLocalServiceUtil.getAssetEntry(question.getProcedureId()).getTitle(locale)%> gestellt</a>
        </aui:container>
    </c:if>
    <aui:container>
        <aui:row>
            <aui:col cssClass="qaQuestionRatingCol" span="1">
                <c:choose>
                    <c:when test="<%= themeDisplay.getUserId() == question.getUserId() %>">
                        <liferay-ui:ratings className="<%=Question.class.getName()%>"
                                            classPK="<%=question.getQuestionID()%>" type="like"
                                            url="blocked"/>
                    </c:when>
                    <c:otherwise>
                        <liferay-ui:ratings className="<%=Question.class.getName()%>"
                                            classPK="<%=question.getQuestionID()%>" type="like"/>
                    </c:otherwise>
                </c:choose>
            </aui:col>
            <aui:col span="11">
                <aui:row>
                    <%=question.getText() %>
                </aui:row>
                <aui:row>
                    <div class="">
                        <c:if test="<%=categories.size() > 0 %>">
                            <div class="qaTagContainer">
                                <strong>Kategorien:</strong>
                                <ul class="qaTags">
                                    <c:forEach items="<%= categories%>" var="category">
                                        <portlet:renderURL var="categoryURL">
                                            <portlet:param name="mvcPath" value="/categories.jsp"/>
                                            <portlet:param name="category" value="${category.name}"/>
                                            <portlet:param name="backURL" value="<%= viewURL%>"/>
                                        </portlet:renderURL>
                                        <%
                                            AssetCategory category = (AssetCategory) pageContext.getAttribute("category");
                                            int code = category.getName().hashCode();
                                            // Modulo fix so that it works for negative integers
                                            int index = (code % tagColors.length + tagColors.length) % tagColors.length;
                                            String color = tagColors[index];
                                        %>
                                        <li style="background: <%= color %>">
                                            <a href="<%= categoryURL %>">${category.name}</a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </c:if>
                        <c:if test="<%=tags.size() > 0 %>">
                            <div class="qaTagContainer">
                                <strong>Tags:</strong>
                                <ul class="qaTags">
                                    <c:forEach items="<%= tags%>" var="tag">
                                        <portlet:renderURL var="tagsURL">
                                            <portlet:param name="mvcPath" value="/tags.jsp"/>
                                            <portlet:param name="tag" value="${tag.name}"/>
                                            <portlet:param name="backURL" value="<%= viewURL%>"/>
                                        </portlet:renderURL>
                                        <%
                                            AssetTag tag = (AssetTag) pageContext.getAttribute("tag");
                                            int code = tag.getName().hashCode();
                                            // Modulo fix so that it works for negative integers
                                            int index = (code % tagColors.length + tagColors.length) % tagColors.length;
                                            String color = tagColors[index];
                                        %>
                                        <li style="background: <%= color %>">
                                            <a href="<%= tagsURL %>">${tag.name}</a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </c:if>
                    </div>

                    <div class="qaAuthorbox">
                        <div>gefragt am <%=sdf.format(question.getCreateDate())%></div>
                        <div class="qaAuthorImage">
                            <img src="<%=author.getPortraitURL(themeDisplay)%>"/>
                        </div>
                        <div class="qaAuthorInfo">
                            <%=author.getFullName()%>
                            <c:if test="<%=authorOrganisations.size() > 0 %>">
                                <br/> <%=authorOrganisations.get(0).getName()%>
                            </c:if>

                        </div>
                    </div>

                    <c:if test="<%=question.getEditedDate() != null && question.getEditedDate().after(question.getCreateDate())%>">
                        <%
                            User questionEditor = UserLocalServiceUtil.getUser(question.getEditedBy());
                            List<Organization> questionEditorOrganizations = questionEditor.getOrganizations();
                        %>
                        <div class="qaAuthorbox">
                            <div>editiert am <%=sdf.format(question.getEditedDate())%></div>
                            <div class="qaAuthorImage">
                                <img src="<%=questionEditor.getPortraitURL(themeDisplay)%>"/>
                            </div>
                            <div class="qaAuthorInfo">
                                <%=questionEditor.getFullName()%>
                                <c:if test="<%=questionEditorOrganizations.size() > 0 %>">
                                    <br/> <%=questionEditorOrganizations.get(0).getName()%>
                                </c:if>

                            </div>
                        </div>
                    </c:if>
                </aui:row>
                <aui:button-row cssClass="qaButtonRow">

                    <c:if test="<%= QuestionsAndAnswersPortlet.questionPermissionContains(permissionChecker, question, "EDIT") %>">
                        <portlet:renderURL var="editQuestionURL">
                            <portlet:param name="mvcPath" value="/editQuestion.jsp"/>
                            <portlet:param name="backURL" value="<%= showQuestionsURL%>"/>
                            <portlet:param name="questionID" value="<%=String.valueOf(question.getQuestionID())%>"/>
                        </portlet:renderURL>
                        <aui:button onClick="<%=editQuestionURL%>" value="Frage bearbeiten"/>
                    </c:if>

                    <c:if test="<%= QuestionsAndAnswersPortlet.questionPermissionContains(permissionChecker, question, "DELETE") %>">
                        <portlet:actionURL name="deleteQuestion" var="deleteQuestionURL">
                            <portlet:param name="questionID" value="<%=String.valueOf(question.getQuestionID())%>"/>
                        </portlet:actionURL>
                        <aui:button onClick="<%=deleteQuestionURL%>" value="Frage l&ouml;schen"/>
                    </c:if>

                    <c:if test="<%= permissionChecker.hasPermission(scopeGroupId, "org.sidate.qanda.model", scopeGroupId, "ADD_ANSWER") %>" >
                        <aui:button id="addNewAnswerButton" value="Frage beantworten"/>
                    </c:if>

                    <aui:script>
                        $("#<portlet:namespace/>addNewAnswerButton").click(function () {
                            $(".main-container").animate({scrollTop: $("#newAnswerFormContainer").position().top}, 500, 'swing');
                        });
                    </aui:script>
                </aui:button-row>
                <aui:row cssClass='<%="qaDiscussionWrapper questionDiscussion_"+question.getQuestionID()%>'>
                    <a id="toggleQuestionComment_<%=question.getQuestionID()%>">neuen Kommentar hinzuf&uuml;gen</a>

                    <aui:script>
                        $("#toggleQuestionComment_<%=question.getQuestionID()%>").click(function () {
                            $(".questionDiscussion_<%=question.getQuestionID()%> .fieldset.add-comment").toggleClass("unhidden");
                        });
                    </aui:script>

                    <portlet:actionURL name="invokeTaglibDiscussion" var="discussionURL" />

                    <% String currentUrl = PortalUtil.getCurrentURL(request); %>


                    <liferay-ui:discussion className="<%=Question.class.getName()%>"
                                           classPK="<%=question.getQuestionID()%>"
                                           formAction="<%=discussionURL%>"
                                           formName='<%="questionForm_"+question.getQuestionID()%>'
                                           ratingsEnabled="<%=false%>"
                                           redirect="<%=currentUrl%>"
                                           userId="<%=themeDisplay.getUserId()%>"
                                           hideControls="<%=false%>"/>
                </aui:row>
            </aui:col>
        </aui:row>
    </aui:container>
    <aui:container cssClass="qaAnswersTitleBarWrapper">

        <div class="qaAnswerTabBarTitle">
            <c:if test="<%=answersSortedByDate.size() != 1%>">
                <h5><%=answersSortedByDate.size()%> Antworten</h5>
            </c:if>
            <c:if test="<%=answersSortedByDate.size() == 1%>">
                <h5><%=answersSortedByDate.size()%> Antwort</h5>
            </c:if>
        </div>

        <liferay-ui:tabs names="Alter,Bewertungen" refresh="false">

            <c:if test="<%= question.getIsAnswered() %>">
                <%
                    Answer answer = AnswerLocalServiceUtil.getAnswer(question.getCorrectAnswerId());
                    User answerAuthor = UserLocalServiceUtil.getUser(answer.getUserId());
                    List<Organization> answerAuthorOrganizations = answerAuthor.getOrganizations();
                    answersSortedByDate.remove(answer);
                    answersSortedByRating.remove(answer);
                %>

                <aui:row cssClass="qaContentContainer">
                    <aui:col cssClass="qaAnswerRatingCol" span="1">
                        <c:choose>
                            <c:when test="<%= themeDisplay.getUserId() == answer.getUserId() %>">
                                <liferay-ui:ratings className="<%=Answer.class.getName()%>"
                                                    classPK="<%=answer.getAnswerID()%>" type="like"
                                                    url="blocked"/>
                            </c:when>
                            <c:otherwise>
                                <liferay-ui:ratings className="<%=Answer.class.getName()%>"
                                                    classPK="<%=answer.getAnswerID()%>" type="like"/>
                            </c:otherwise>
                        </c:choose>
                        <span class="qaCorreckAnswerCheckmark glyphicon glyphicon-ok"/>
                    </aui:col>
                    <aui:col span="11">
                        <aui:row>
                            <%= answer.getText() %>
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
                                <portlet:actionURL name="unsetCorrectAnswer" var="unsetCorrectAnswerURL">
                                    <portlet:param name="mvcPath" value="/showQuestion.jsp"/>
                                    <portlet:param name="backURL" value="<%= backURL%>"/>
                                    <portlet:param name="questionID" value="<%=String.valueOf(question.getQuestionID())%>"/>
                                </portlet:actionURL>
                                <aui:button onClick="<%=unsetCorrectAnswerURL%>" value="Akzeptierte Antwort zur&#252;ckziehen"/>
                            </c:if>


                        </aui:button-row>
                        <aui:row cssClass='<%="qaDiscussionWrapper answerDiscussion_"+answer.getAnswerID()%>'>
                            <a id="toggleAnswerComment_<%=answer.getAnswerID()%>">neuen Kommentar hinzuf&uuml;gen</a>

                            <aui:script>
                                $("#toggleAnswerComment_<%=answer.getAnswerID()%>").click(function () {
                                $(".answerDiscussion_<%=answer.getAnswerID()%> .fieldset.add-comment").toggleClass("unhidden");
                                });
                            </aui:script>

                            <portlet:actionURL name="invokeTaglibDiscussion" var="answerDiscussionURL" />

                            <% String answerURL = PortalUtil.getCurrentURL(request); %>

                            <liferay-ui:discussion className="<%=Answer.class.getName()%>"
                                                   classPK="<%=answer.getAnswerID()%>"
                                                   formAction="<%=answerDiscussionURL%>"
                                                   formName='<%="answerForm_"+answer.getAnswerID()%>'
                                                   ratingsEnabled="<%=false%>"
                                                   redirect="<%=answerURL%>"
                                                   userId="<%=themeDisplay.getUserId()%>" />

                        </aui:row>
                    </aui:col>
                </aui:row>
            </c:if>

            <liferay-ui:section>
                <aui:container cssClass="qaAnswersWrapper">
                    <% String identifier = "date"; %>
                    <c:forEach var="answer" items="<%=answersSortedByDate%>">
                        <%@ include file="_answer.jsp" %>
                    </c:forEach>

                </aui:container>
            </liferay-ui:section>

            <liferay-ui:section>
                <aui:container cssClass="qaAnswersWrapper">
                    <% String identifier = "rating"; %>
                    <c:forEach var="answer" items="<%=answersSortedByRating%>">
                        <%@ include file="_answer.jsp" %>
                    </c:forEach>
                </aui:container>
            </liferay-ui:section>
        </liferay-ui:tabs>
    </aui:container>

    <c:if test="<%= permissionChecker.hasPermission(scopeGroupId, "org.sidate.qanda.model", scopeGroupId, "ADD_ANSWER") %>" >
        <portlet:actionURL name="newAnswer" var="newAnswerURL">
            <portlet:param name="questionID" value="<%=String.valueOf(question.getQuestionID())%>"/>
            <portlet:param name="redirectURL" value="<%=showQuestionsURL%>"/>
        </portlet:actionURL>
        <aui:container>
            <aui:form action="<%= newAnswerURL %>" name="AnswerForm">
                <h5 id="newAnswerFormContainer">Ihre Antwort</h5>
                <liferay-ui:input-editor name="text"/>
                <aui:script>
                    function <portlet:namespace />initEditor() {
                        return "";
                    }
                </aui:script>
                <aui:button-row>
                    <aui:button value="Antwort absenden" type="submit"/>
                </aui:button-row>
            </aui:form>
        </aui:container>
    </c:if>

</aui:container>