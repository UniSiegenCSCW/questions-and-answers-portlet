<%@ page import="org.sidate.questions_and_answers.model.Question" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="org.sidate.questions_and_answers.service.QuestionLocalServiceUtil" %>
<%@ page import="org.sidate.questions_and_answers.model.Answer" %>
<%@ page import="org.sidate.questions_and_answers.service.AnswerLocalServiceUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil" %>
<%@ page import="com.liferay.asset.kernel.model.AssetEntry" %>
<%@ page import="com.liferay.asset.kernel.model.AssetTag" %>
<%@ page import="com.liferay.asset.kernel.model.AssetCategory" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.liferay.portal.kernel.service.UserLocalServiceUtil" %>
<%@ page import="com.liferay.portal.kernel.model.User" %>
<%@ page import="com.liferay.portal.kernel.util.PortalUtil" %>
<%@ include file="init.jsp" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"/>

<portlet:renderURL var="viewURL">
    <portlet:param name="mvcPath" value="/view.jsp"/>
</portlet:renderURL>

<%
    long questionID = ParamUtil.getLong(renderRequest, "questionID");
    String backURL = ParamUtil.getString(renderRequest, "backURL");
    Question question = QuestionLocalServiceUtil.getQuestion(questionID);
    AssetEntry asset = AssetEntryLocalServiceUtil.getEntry(Question.class.getName(), questionID);
    List<AssetTag> tags = asset.getTags();
    List<AssetCategory> categories = asset.getCategories();
    List<Answer> answerList = AnswerLocalServiceUtil.getAnswersForQuestion(questionID);

    SimpleDateFormat sdf = new SimpleDateFormat("dd. MMM yyyy 'um' hh:mm");

    User author = UserLocalServiceUtil.getUser(question.getUserId());

    long groupId = scopeGroupId;
    String name = portletDisplay.getRootPortletId();
    String primKey = portletDisplay.getResourcePK();
    String actionId = "ADD_SOMETHING";

%>

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
                    <div class="">
                        <div class="qaAuthorbox">
                            <div>gefragt am <%=sdf.format(question.getCreateDate())%></div>
                            <div class="qaAuthorImage">
                                <img src="<%=author.getPortraitURL(themeDisplay)%>"/>
                            </div>
                            <div class="qaAuthorInfo">
                                <%=author.getFullName()%><br/>
                                STADTWERKE XYZ
                            </div>
                        </div>
                    </div>
                </aui:row>
                <aui:row>

                </aui:row>
                <aui:row cssClass='<%="qaDiscussionWrapper questionDiscussion_"+question.getQuestionID()%>'>
                    <a id="toggleQuestionComment_<%=question.getQuestionID()%>">neuen Kommentar hinzuf&uuml;gen</a>
                    <script type="application/javascript">
                            $("#toggleQuestionComment_<%=question.getQuestionID()%>").click(function () {
                                $(".questionDiscussion_<%=question.getQuestionID()%> .fieldset.add-comment").toggleClass("unhidden");
                            });
                    </script>
                    <portlet:actionURL name="invokeTaglibDiscussion" var="discussionURL" />

                    <%
                        String currentUrl = PortalUtil.getCurrentURL(request);
                    %>

                    <liferay-ui:discussion className="<%=Question.class.getName()%>"
                                           classPK="<%=question.getQuestionID()%>"
                                           formAction="<%=discussionURL%>"
                                           formName='<%="questionForm_"+question.getQuestionID()%>'
                                           ratingsEnabled="<%=false%>"
                                           redirect="<%=currentUrl%>"
                                           userId="<%=themeDisplay.getUserId()%>" />

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
            <h5><%=answerList.size()%> Antworten</h5>
        </div>
    </aui:container>
    <aui:container cssClass="qaAnswersWrapper">
        <c:forEach var="answer" items="<%=answerList%>">
            <%
                Answer answer = (Answer)pageContext.getAttribute("answer");
                User answerAuthor = UserLocalServiceUtil.getUser(question.getUserId());
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
                            <div>geantwortet am <%=sdf.format(answer.getCreateDate())%></div>
                            <div class="qaAuthorImage">
                                <img src="<%=answerAuthor.getPortraitURL(themeDisplay)%>"/>
                            </div>
                            <div class="qaAuthorInfo">
                                <%=answerAuthor.getFullName()%><br/>
                                STADTWERKE XYZ
                            </div>
                        </div>
                    </aui:row>

                    <aui:row cssClass='<%="qaDiscussionWrapper answerDiscussion_"+answer.getAnswerID()%>'>
                        <a id="toggleAnswerComment_<%=answer.getAnswerID()%>">neuen Kommentar hinzuf&uuml;gen</a>
                        <script type="application/javascript">
                            $("#toggleAnswerComment_<%=answer.getAnswerID()%>").click(function () {
                                $(".answerDiscussion_<%=answer.getAnswerID()%> .fieldset.add-comment").toggleClass("unhidden");
                            });
                        </script>
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
</aui:container>