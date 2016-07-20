<%@ page import="org.sidate.questions_and_answers.model.Question" %>
<%@ page import="com.liferay.asset.kernel.model.AssetEntry" %>
<%@ page import="com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="com.liferay.asset.kernel.model.AssetTag" %>
<%@ page import="com.liferay.asset.kernel.model.AssetCategory" %>
<%@ page import="org.sidate.questions_and_answers.model.Answer" %>
<%@ page import="org.sidate.questions_and_answers.service.AnswerLocalServiceUtil" %>
<%@ page import="com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil" %>
<%@ page import="com.liferay.ratings.kernel.model.RatingsStats" %>
<%@ page import="org.sidate.questions_and_answers.util.QAUtils" %>
<%@ page import="com.liferay.portal.kernel.model.User" %>
<%@ page import="com.liferay.portal.kernel.service.UserLocalServiceUtil" %>
<%@ include file="init.jsp" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"/>

<jsp:useBean id="questions" class="java.util.ArrayList" scope="request"/>

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

    <% String tabNames = "Neue Fragen,Beste Fragen"; %>
    <liferay-ui:tabs
            names="<%= tabNames %>"
    />


    <aui:container cssClass="qaQuestionsOverviewContainer">
        <c:forEach var="question" items="${questions}">

            <%

                Question question = (Question)pageContext.getAttribute("question");
                AssetEntry asset = AssetEntryLocalServiceUtil.getEntry(Question.class.getName(), question.getQuestionID());
                RatingsStats ratingsStats = RatingsStatsLocalServiceUtil.getStats(Question.class.getName(), question.getQuestionID());
                List<AssetTag> tags = asset.getTags();
                List<AssetCategory> categories = asset.getCategories();
                List<Answer> answers = AnswerLocalServiceUtil.getAnswersForQuestion(question.getQuestionID());

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
                            if (asset.getViewCount()<1000) {
                                viewCount = String.valueOf(asset.getViewCount());
                            } else {
                                viewCount = String.valueOf(Math.round(asset.getViewCount()/100.0)/10.0)+"k";
                            }
                        %>
                        <div class="qaStatCounterBox">
                            <div class="qaCounterLabel">Ansichten</div>
                            <div class="qaCounterValue">
                                <%=viewCount%>
                            </div>
                        </div>
                        <div class="qaStatCounterBox
                            <c:if test="${question.correctAnswerId != 0}">
                                answered
                            </c:if>
                        ">
                            <div class="qaCounterLabel">Antworten</div>
                            <div class="qaCounterValue"><%=answers.size()%></div>
                        </div>
                        <div class="qaStatCounterBox">
                            <div class="qaCounterLabel">Wertungen</div>
                            <div class="qaCounterValue">
                                <%=(int) ratingsStats.getTotalScore()%>
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
                        %>
                        <c:choose>
                            <c:when test="<%=latestAnswer != null &&
                                             latestAnswer.getCreateDate().after(question.getCreateDate()) &&
                                             (question.getModifiedDate() == null ||
                                             latestAnswer.getCreateDate().after(question.getModifiedDate()))%>">
                                <span class="qaDateTime">beantwortet <%=QAUtils.getTimeDifferenceString(latestAnswer.getCreateDate())%> von <%=latestAnswerAuthor.getFullName()%></span>

                            </c:when>
                            <c:when test="<%=question.getModifiedDate() != null &&
                                             question.getModifiedDate().after(question.getCreateDate())%>">
                                <span class="qaDateTime">editiert <%=QAUtils.getTimeDifferenceString(question.getModifiedDate())%> von <%=editor.getFullName()%></span>
                            </c:when>
                            <c:otherwise>
                                <span class="qaDateTime">gefragt <%=QAUtils.getTimeDifferenceString(question.getCreateDate())%> von <%=author.getFullName()%></span>
                            </c:otherwise>
                        </c:choose>
                    </aui:col>
                </aui:row>
            </aui:container>
        </c:forEach>
    </aui:container>
</aui:container>