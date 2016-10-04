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
<%@ include file="init.jsp" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"/>

<jsp:useBean id="questions" class="java.util.ArrayList" scope="request"/>
<jsp:useBean id="questionsSortedByRating" class="java.util.ArrayList" scope="request"/>

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


<aui:container cssClass="qaQuestionOverviewWrapper">
    <aui:button-row>
        <aui:button cssClass="pull-right" onClick="<%= testURL%>" value="Go to testing view"></aui:button>
        <aui:button cssClass="pull-right" onClick="<%= newQuestionURL%>" value="Neue Frage stellen"></aui:button>
    </aui:button-row>

    <liferay-ui:tabs names="Neue Fragen,Beste Fragen" refresh="false" tabsValues="Neue Fragen,Beste Fragen">
        <liferay-ui:section>
        <%-- Questions will be sorted by date by default --%>
        <% Collections.reverse(questions); %>

        <aui:container cssClass="qaQuestionsOverviewContainer">
                <c:forEach var="question" items="${questions}">

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
                                                <li>${tag.name}</li>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </c:if>
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
        </liferay-ui:section>

        <liferay-ui:section>
        <aui:container cssClass="qaQuestionsOverviewContainer">
                <c:forEach var="question" items="${questionsSortedByRating}">

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
                                                <li>${tag.name}</li>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </c:if>
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
        </liferay-ui:section>
    </liferay-ui:tabs>
</aui:container>