<%@ page import="org.sidate.qanda.model.Question" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="org.sidate.qanda.service.QuestionLocalServiceUtil" %>
<%@ page import="org.sidate.qanda.model.Answer" %>
<%@ page import="org.sidate.qanda.service.AnswerLocalServiceUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil" %>
<%@ page import="com.liferay.asset.kernel.model.AssetEntry" %>
<%@ page import="com.liferay.asset.kernel.model.AssetTag" %>
<%@ page import="com.liferay.asset.kernel.model.AssetCategory" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.liferay.portal.kernel.service.UserLocalServiceUtil" %>
<%@ page import="com.liferay.portal.kernel.model.User" %>
<%@ page import="com.liferay.portal.kernel.util.PortalUtil" %>
<%@ page import="com.liferay.portal.kernel.model.Organization" %>
<%@ include file="init.jsp" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"/>

<%
    long questionID = ParamUtil.getLong(renderRequest, "questionID");
    String backURL = ParamUtil.getString(renderRequest, "backURL");
    Question question = QuestionLocalServiceUtil.getQuestion(questionID);
    List<AssetTag> tags = question.getTags();
    List<AssetCategory> categories = question.getCategories();
    List<Answer> answerList = AnswerLocalServiceUtil.getAnswersForQuestion(questionID);
    AssetEntryLocalServiceUtil.incrementViewCounter(themeDisplay.getUserId(), Question.class.getName(), question.getQuestionID());

    SimpleDateFormat sdf = new SimpleDateFormat("dd. MMM yyyy 'um' hh:mm");

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
<portlet:renderURL var="editQuestionURL">
    <portlet:param name="mvcPath" value="/editQuestion.jsp"/>
    <portlet:param name="backURL" value="<%= showQuestionsURL%>"/>
    <portlet:param name="questionID" value="<%=String.valueOf(question.getQuestionID())%>"/>
</portlet:renderURL>

<liferay-ui:header
        backURL="<%= backURL %>"
        title='<%=question.getTitle() %>'
/>
<aui:container cssClass="qaQuestionWrapper">
    <aui:container>
        <aui:row>
            <aui:col cssClass="qaQuestionRatingCol" span="1">
                <liferay-ui:ratings className="<%=Question.class.getName()%>"
                                    classPK="<%=question.getQuestionID()%>" type="like" />
            </aui:col>
            <aui:col span="11">
                <aui:row>
                    <%=question.getText() %>
                </aui:row>
                <aui:row>
                    <div class="">
                        <c:if test="<%=categories.size() > 0 %>">
                            <div class="qaCategoryContainer">
                                <strong>Kategorien:</strong>
                                <ul class="qaCategories">
                                    <c:forEach items="<%= categories%>" var="category">
                                        <li>${category.name}</li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </c:if>
                        <c:if test="<%=tags.size() > 0 %>">
                            <div class="qaTagContainer">
                                <strong>Tags:</strong>
                                <ul class="qaTags">
                                    <c:forEach items="<%= tags%>" var="tag">
                                        <li>${tag.name}</li>
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

                    <c:if test="<%=question.getModifiedDate() != null && question.getModifiedDate().after(question.getCreateDate())%>">
                        <%
                            User questionEditor = UserLocalServiceUtil.getUser(question.getEditedBy());
                            List<Organization> questionEditorOrganizations = questionEditor.getOrganizations();
                        %>
                        <div class="qaAuthorbox">
                            <div>editiert am <%=sdf.format(question.getModifiedDate())%></div>
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
                    <aui:button onClick="<%=editQuestionURL%>" value="Frage bearbeiten"/>
                    <portlet:actionURL name="deleteQuestion" var="deleteQuestionURL">
                        <portlet:param name="questionID" value="<%=String.valueOf(question.getQuestionID())%>"/>
                    </portlet:actionURL>
                    <aui:button onClick="<%=deleteQuestionURL%>" value="Frage l&ouml;schen"/>
                    <aui:button id="addNewAnswerButton" value="Frage beantworten"/>
                    <aui:script>
                        $("#<portlet:namespace/>addNewAnswerButton").click(function () {
                            $(".main-container").animate({scrollTop: $("#newAnswerFormContainer").position().top}, 500, 'swing');
                        });
                    </aui:script>
                </aui:button-row>
                <aui:row cssClass='<%="qaDiscussionWrapper questionDiscussion_"+question.getQuestionID()%>'>
                    <a id="toggleQuestionComment_<%=question.getQuestionID()%>">neuen Kommentar hinzuf&uuml;gen</a>

                    <portlet:actionURL name="invokeTaglibDiscussion" var="discussionURL" />

                    <%
                        String currentUrl = PortalUtil.getCurrentURL(request);
                    %>

                    <aui:script>
                        $("#toggleQuestionComment_<%=question.getQuestionID()%>").click(function () {
                            $(".questionDiscussion_<%=question.getQuestionID()%> .fieldset.add-comment").toggleClass("unhidden");
                        });
                    </aui:script>

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
    <% String tabNames = "Aktiv, Alter, Bewertungen"; %>
        <liferay-ui:tabs
                names="<%= tabNames %>"
        />
        <div class="qaAnswerTabBarTitle">
        <c:if test="<%=answerList.size() != 1%>">
                <h5><%=answerList.size()%> Antworten</h5>
        </c:if>
        <c:if test="<%=answerList.size() == 1%>">
            <h5><%=answerList.size()%> Antwort</h5>
        </c:if>
        </div>
    </aui:container>
    <aui:container cssClass="qaAnswersWrapper">
        <c:forEach var="answer" items="<%=answerList%>">
            <%
                Answer answer = (Answer)pageContext.getAttribute("answer");
                User answerAuthor = UserLocalServiceUtil.getUser(question.getUserId());
                List<Organization> answerAuthorOrganizations = answerAuthor.getOrganizations();

            %>
            <aui:row cssClass="qaContentContainer">
                <aui:col cssClass="qaAnswerRatingCol" span="1">
                    <liferay-ui:ratings className="<%=Answer.class.getName()%>"
                                        classPK="<%=answer.getAnswerID()%>" type="like" />
                    <c:if test="<%=question.getCorrectAnswerId() == answer.getAnswerID()%>">
                        <div>
                            <span class="qaCorreckAnswerCheckmark glyphicon glyphicon-ok"/>
                        </div>
                    </c:if>
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
                        <c:if test="<%=answer.getModifiedDate() != null && answer.getModifiedDate().after(answer.getCreateDate())%>">
                            <%
                                User answerEditor = UserLocalServiceUtil.getUser(question.getUserId());
                                List<Organization> answerEditorOrganizations = answerEditor.getOrganizations();
                            %>
                            <div class="qaAuthorbox">
                                <div>editiert am <%=sdf.format(answer.getModifiedDate())%></div>
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

                        <portlet:renderURL var="editAnswerURL">
                            <portlet:param name="mvcPath" value="/editAnswer.jsp"/>
                            <portlet:param name="backURL" value="<%= showQuestionsURL%>"/>
                            <portlet:param name="answerID" value="<%=String.valueOf(answer.getAnswerID())%>"/>
                        </portlet:renderURL>

                        <aui:button onClick="<%=editAnswerURL%>" value="Antwort bearbeiten"/>
                        <portlet:actionURL name="deleteAnswer" var="deleteAnswerURL">
                            <portlet:param name="answerID" value="<%=String.valueOf(answer.getAnswerID())%>"/>
                            <portlet:param name="redirectURL" value="<%=showQuestionsURL%>"/>
                        </portlet:actionURL>
                        <aui:button onClick="<%=deleteAnswerURL%>" value="Antwort l&ouml;schen"/>
                    </aui:button-row>
                    <aui:row cssClass='<%="qaDiscussionWrapper answerDiscussion_"+answer.getAnswerID()%>'>
                        <a id="toggleAnswerComment_<%=answer.getAnswerID()%>">neuen Kommentar hinzuf&uuml;gen</a>

                        <aui:script>
                            $("#toggleAnswerComment_<%=answer.getAnswerID()%>").click(function () {
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
        </c:forEach>
    </aui:container>
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
</aui:container>