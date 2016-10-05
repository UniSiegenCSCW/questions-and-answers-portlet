<%@ page import="org.sidate.qanda.model.Question" %>
<%@ page import="com.liferay.asset.kernel.model.AssetEntry" %>
<%@ page import="com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="com.liferay.asset.kernel.model.AssetTag" %>
<%@ page import="com.liferay.asset.kernel.model.AssetCategory" %>
<%@ page import="org.sidate.qanda.model.Answer" %>
<%@ page import="org.sidate.qanda.service.AnswerLocalServiceUtil" %>
<%@ page import="com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil" %>
<%@ page import="com.liferay.ratings.kernel.model.RatingsStats" %>
<%@ page import="com.liferay.portal.kernel.model.User" %>
<%@ page import="com.liferay.portal.kernel.service.UserLocalServiceUtil" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.liferay.portal.kernel.model.Organization" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.sidate.qanda.service.QuestionLocalServiceUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ include file="init.jsp" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"/>

<jsp:useBean id="questionsFilteredByTag" class="java.util.ArrayList" scope="request"/>


<%
    String backURL = ParamUtil.getString(renderRequest, "backURL");
    String tag = ParamUtil.getString(renderRequest, "tag");
%>

<portlet:renderURL var="mainViewURL">
    <portlet:param name="mvcPath" value="/view.jsp"/>
</portlet:renderURL>

<portlet:renderURL var="newQuestionURL">
    <portlet:param name="mvcPath" value="/editQuestion.jsp"/>
    <portlet:param name="backURL" value="<%= mainViewURL%>"/>
</portlet:renderURL>

<portlet:renderURL var="testURL">
    <portlet:param name="mvcPath" value="/test.jsp"/>
</portlet:renderURL>

<liferay-ui:header
        backURL="<%= backURL %>"
        title='<%= tag %>'
/>

<aui:container cssClass="qaQuestionOverviewWrapper">

    <%-- Questions will be sorted by date by default --%>
    <% Collections.reverse(questionsFilteredByTag); %>

    <aui:container cssClass="qaQuestionsOverviewContainer">
        <c:forEach var="question" items="${questionsFilteredByTag}">

            <%
                Question question = (Question) pageContext.getAttribute("question");
                List<AssetTag> tags = question.getTags();
                List<AssetCategory> categories = question.getCategories();
                List<Answer> answers = AnswerLocalServiceUtil.getAnswersForQuestion(question.getQuestionID());
                int views = question.getViewCount();
            %>
            <portlet:renderURL var="showQuestionURL">
                <portlet:param name="mvcPath" value="/showQuestion.jsp"/>
                <portlet:param name="backURL" value="<%= mainViewURL%>"/>
                <portlet:param name="questionID" value="${question.questionID}"/>
            </portlet:renderURL>

            <aui:container cssClass="qaQuestionEntryContainer">
                <aui:row>
                    <aui:col span="8">
                        <aui:row>
                            <aui:col>
                                <h5><a href="<%= showQuestionURL%>">${question.title}</a></h5>
                            </aui:col>
                        </aui:row>
                    </aui:col>
                    <aui:col span="4">
                        <%
                            String viewCount = "";
                            if (views < 1000) {
                                viewCount = String.valueOf(views);
                            } else {
                                viewCount = String.valueOf(Math.round(views / 100.0) / 10.0) + "k";
                            }
                        %>
                        <div class="qaStatCounterBox">
                            <div class="qaCounterLabel">Ansichten</div>
                            <div class="qaCounterValue">
                                <%=viewCount%>
                            </div>
                        </div>

                        <div class="qaStatCounterBox
                                    <c:if test="${question.getIsAnswered()}">
                                        answered
                                    </c:if>
                                ">
                            <div class="qaCounterLabel">Antworten</div>
                            <div class="qaCounterValue"><%=answers.size()%></div>
                        </div>

                        <div class="qaStatCounterBox">
                            <div class="qaCounterLabel">Wertungen</div>
                            <div class="qaCounterValue">
                                <%=(int) question.getRating()%>
                            </div>
                        </div>
                    </aui:col>
                </aui:row>
                <aui:row>
                    <aui:col>
                        <c:if test="<%=tags.size() > 0 %>">
                            <div class="qaTagContainer">
                                <strong>Tags:</strong>
                                <ul class="qaTags">
                                    <c:forEach items="<%= tags%>" var="tag">
                                        <portlet:renderURL var="tagsURL">
                                            <portlet:param name="mvcPath" value="/tags.jsp"/>
                                            <portlet:param name="tag" value="${tag.name}"/>
                                            <portlet:param name="backURL" value="<%= mainViewURL%>"/>
                                        </portlet:renderURL>

                                        <li><a href="<%= tagsURL %>">${tag.name}</a></li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </c:if>
                        <c:if test="<%=categories.size() > 0 %>">
                            <div class="qaCategoryContainer">
                                <strong>Kategorien:</strong>
                                <ul class="qaCategories">
                                    <c:forEach items="<%= categories%>" var="category">
                                        <portlet:renderURL var="categoryURL">
                                            <portlet:param name="mvcPath" value="/categories.jsp"/>
                                            <portlet:param name="category" value="${category.name}"/>
                                            <portlet:param name="backURL" value="<%= mainViewURL%>"/>
                                        </portlet:renderURL>

                                        <li><a href="<%= categoryURL %>">${category.name}</a></li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </c:if>
                    </aui:col>
                </aui:row>
                <aui:row>
                    <aui:col>
                        <%
                            Answer latestAnswer = null;
                            User author = UserLocalServiceUtil.getUser(question.getUserId());
                            User latestAnswerAuthor = null;
                            User editor = null;

                            if (answers.size() > 0){
                                latestAnswer = answers.get(answers.size()-1);
                                latestAnswerAuthor = UserLocalServiceUtil.getUser(latestAnswer.getUserId());
                            }

                            if (question.getEditedBy() != 0) {
                                editor = UserLocalServiceUtil.getUser(question.getEditedBy());
                            }


                            boolean questionEdited = question.getEditedDate() != null;
                            boolean questionHasAnswer = latestAnswer != null;
                        %>
                        <c:choose>
                            <c:when test="<%= questionHasAnswer && ( !questionEdited || latestAnswer.getCreateDate().after(question.getEditedDate()) ) %>">
                                <span class="qaDateTime">beantwortet <%=latestAnswer.getTimeSinceCreated()%> von <%=latestAnswerAuthor.getFullName()%></span>
                            </c:when>
                            <c:when test="<%= questionEdited %>">
                                <span class="qaDateTime">editiert <%=question.getTimeSinceEdited()%> von <%=editor.getFullName()%></span>
                            </c:when>
                            <c:otherwise>
                                <span class="qaDateTime">gefragt <%=question.getTimeSinceCreated()%> von <%=author.getFullName()%></span>
                            </c:otherwise>
                        </c:choose>
                    </aui:col>
                </aui:row>
            </aui:container>
        </c:forEach>
    </aui:container>


</aui:container>