package org.sidate.qanda.portlet;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.exportimport.kernel.staging.permission.StagingPermissionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.sidate.qanda.exception.EmptyAnswerTextException;
import org.sidate.qanda.exception.EmptyQuestionTextException;
import org.sidate.qanda.exception.EmptyQuestionTitleException;
import org.sidate.qanda.model.Answer;
import org.sidate.qanda.model.Question;
import org.sidate.qanda.model.QuestionModel;
import org.sidate.qanda.service.AnswerLocalServiceUtil;
import org.sidate.qanda.service.QuestionLocalServiceUtil;

import javax.portlet.*;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toList;


@Component(
        immediate = true,
        property = {
                "com.liferay.portlet.display-category=category.sample",
                "com.liferay.portlet.instanceable=true",
                "javax.portlet.display-name=Questions and Answers",
                "javax.portlet.security-role-ref=power-user,user",
                "javax.portlet.init-param.template-path=/",
                "javax.portlet.init-param.view-template=/view.jsp",
                "javax.portlet.resource-bundle=content.Language"
        },
        service = Portlet.class
)

public class QuestionsAndAnswersPortlet extends MVCPortlet {

    private static final Log log = LogFactoryUtil.getLog(QuestionsAndAnswersPortlet.class);

    // #### Question ####

    public void newQuestion(ActionRequest request, ActionResponse response) {
        ServiceContext serviceContext = getServiceContext(request, Question.class.getName());
        String title = ParamUtil.getString(request, "title");
        String text = ParamUtil.getString(request, "text");
        boolean isQuestionToProcedure = ParamUtil.getString(request, "isQuestionToProcedure").equals("crqp");
        long procedureId = 0;
        if(isQuestionToProcedure)
            procedureId = Long.valueOf(ParamUtil.getString(request, "procedureId"));

        if (Validator.isNull(title)) {
            handleError(request, new EmptyQuestionTitleException(), "An error occurred during newQuestion");
            PortalUtil.copyRequestParameters(request, response);
            response.setRenderParameter("mvcPath", "/editQuestion.jsp");
            return;
        }
        if (Validator.isNull(text)) {
            handleError(request, new EmptyQuestionTextException(), "An error occurred during newQuestion");
            PortalUtil.copyRequestParameters(request, response);
            response.setRenderParameter("mvcPath", "/editQuestion.jsp");
            return;
        }
        try {
            QuestionLocalServiceUtil.addQuestion(title, text, isQuestionToProcedure, procedureId, serviceContext);
        } catch (PortalException e) {
            handleError(request, e, "An error occured during newQuestion");
            return;
        }
        SessionMessages.add(request, "questionAdded");
    }


    public void editQuestion(ActionRequest request, ActionResponse response) {
        String title = ParamUtil.getString(request, "title");
        String text = ParamUtil.getString(request, "text");
        long questionId = ParamUtil.getLong(request, "questionID");
        ServiceContext serviceContext = getServiceContext(request, Question.class.getName());
        if (Validator.isNull(title)) {
            handleError(request, new EmptyQuestionTitleException(), "An error occurred during editQuestion");
            return;
        }
        if (Validator.isNull(text)) {
            handleError(request, new EmptyQuestionTextException(), "An error occurred during editQuestion");
            return;
        }
        try {
            QuestionLocalServiceUtil.editQuestion(questionId, title, text, serviceContext);
        } catch (PortalException e) {
            handleError(request, e, "An error occurred during editQuestion");
            return;
        }
        SessionMessages.add(request, "questionEdited");
        redirectToRedirectUrl(request, response);
    }


    public void deleteQuestion(ActionRequest request, ActionResponse response) {
        ServiceContext serviceContext = getServiceContext(request, Question.class.getName());
        long questionId = ParamUtil.getLong(request, "questionID");
        QuestionLocalServiceUtil.deleteQuestion(questionId, serviceContext);
    }


    /**
     * Returns the questions with a specified AssetCategory name passed via ParamUtil. This method first searches for
     * the AssetCategory with the given name and if exists, filters the questions by that ID.
     * <p>
     * Annotation: This method allows querying the questions by category name, not ID! It depends on the GUI layout
     * whether the ID or name will be used.
     */
    private void getQuestionsFilteredByCategory(RenderRequest request) {
        ServiceContext serviceContext = getServiceContext(request, Question.class.getName());
        List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
        String categoryName = ParamUtil.getString(request, "category");
        Optional<AssetCategory> category = getCategoryByName(categoryName);
        if(!category.isPresent()){
            return;
        }
        long categoryId = category.get().getCategoryId();
        List<Question> filteredQuestions = questions.stream()
                .filter(question -> filterByCategoryId(question, categoryId))
                .collect(toList());
        request.setAttribute("questionsFilteredByCategory",  new ArrayList<>(filteredQuestions));
        log.info(filteredQuestions.size() + " questions filtered by category " + categoryId
                + " have been passed to renderRequest");
    }


    private Optional<AssetCategory> getCategoryByName(String categoryName){
        List<AssetCategory> categories = AssetCategoryLocalServiceUtil.getCategories();
        return categories.parallelStream()
                .filter(category -> category.getName().equals(categoryName)).findFirst();
    }


    public List<Question> getQuestionsFilteredByTag(String tag, RenderRequest renderRequest) {
        ServiceContext serviceContext = getServiceContext(renderRequest, Question.class.getName());
        List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
        return questions.stream()
                .filter(question -> filterByTagName(question, tag))
                .collect(toList());
    }

    /**
     * Returns the questions with a specified Tag name passed via ParamUtil. This method filters the questions by that
     * tag, so if a non existing tag is supplied it will return an empty List.
     */
    private void getQuestionsFilteredByTag(RenderRequest renderRequest) {
        ServiceContext serviceContext = getServiceContext(renderRequest, Question.class.getName());
        List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
        String tagToFilter = ParamUtil.getString(renderRequest, "tag");
        List<Question> filteredQuestions = questions.stream()
                .filter(question -> filterByTagName(question, tagToFilter))
                .collect(toList());
        renderRequest.setAttribute("questionsFilteredByTag",  new ArrayList<>(filteredQuestions));
        log.info(filteredQuestions.size() + " questions filtered by tag " + tagToFilter
                + " have been passed to renderRequest");
    }

    /**
     * This method returns an List of questions sorted by their rating.
     * If two questions have the same rating, they are sorted by their date (older > newer)
     * Questions that are older than one month
     * are dismissed.
     *
     * @return An List of sorted questions.
     */
    private List<Question> getQuestionsSortedByRating(ServiceContext serviceContext) {
        List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
        Comparator<Question> byRatingAndDate = Comparator.comparing(Question::getRating)
                .thenComparing(QuestionModel::getCreateDate);
        return questions.stream()
                .filter(isRecent())
                .sorted(byRatingAndDate.reversed())
                .collect(toList());
    }

    private Predicate<Question> isRecent() {
        LocalDate currentDate = LocalDate.now();
        return question -> Instant.ofEpochMilli(question.getCreateDate()
                .getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .isAfter(currentDate.minusMonths(1));
    }

    private boolean filterByCategoryId(Question question, long idToFilter) {
        long[] categoryIds = question.getCategoryIds();
        return categoryIds != null && LongStream.of(categoryIds).anyMatch(categoryId -> categoryId == idToFilter);
    }

    private boolean filterByTagName(Question question, String tagToFilter) {
        String[] tagNames = question.getTagNames();
        return tagNames != null && Arrays.stream(tagNames).anyMatch(tag -> tag.equals(tagToFilter));
    }

    public void unsetCorrectAnswer(ActionRequest request, ActionResponse response) {
        long questionId = ParamUtil.getLong(request, "questionID");
        QuestionLocalServiceUtil.unsetCorrectAnswer(questionId);
    }

    public void setCorrectAnswer(ActionRequest request, ActionResponse response) {
        long questionId = ParamUtil.getLong(request, "questionID");
        long answerId = ParamUtil.getLong(request, "answerID");
        ServiceContext serviceContext = getServiceContext(request, Answer.class.getName());
        try {
            QuestionLocalServiceUtil.setCorrectAnswer(answerId, questionId, serviceContext);
        } catch (PortalException e) {
            e.printStackTrace();
        }
    }

    // #### Answers ####

    public void newAnswer(ActionRequest request, ActionResponse response) {
        String text = ParamUtil.getString(request, "text");
        if (Validator.isNull(text)) {
            handleError(request, new EmptyAnswerTextException(), "Answer text is empty.");
            return;
        }
        long questionId = ParamUtil.getLong(request, "questionID");
        ServiceContext serviceContext = getServiceContext(request, Answer.class.getName());
        AnswerLocalServiceUtil.addAnswer(text, questionId, serviceContext);
        SessionMessages.add(request, "answerAdded");
        redirectToRedirectUrl(request, response);
    }

    public void editAnswer(ActionRequest request, ActionResponse response) {
        long answerId = ParamUtil.getLong(request, "answerID");
        String text = ParamUtil.getString(request, "text");
        ServiceContext serviceContext = getServiceContext(request, Question.class.getName());
        if (Validator.isNull(text)) {
            handleError(request, new EmptyAnswerTextException(), "Error during editAnswer");
            return;
        }
        AnswerLocalServiceUtil.editAnswer(answerId, text, serviceContext);
        SessionMessages.add(request, "answerEdited");
        redirectToRedirectUrl(request, response);
    }

    public void deleteAnswer(ActionRequest request, ActionResponse response) {
        ServiceContext serviceContext = getServiceContext(request, Answer.class.getName());
        long answerId = ParamUtil.getLong(request, "answerID");
        try {
            AnswerLocalServiceUtil.deleteAnswer(answerId, serviceContext);
        } catch (PortalException e) {
            handleError(request, e, "deleteAnswer");
            return;
        }
        redirectToRedirectUrl(request, response);
    }

    // ### UTIL ###

    @Override
    public void render(RenderRequest renderRequest, RenderResponse renderResponse) {
        ServiceContext serviceContext = getServiceContext(renderRequest, Question.class.getName());
        long groupID = serviceContext.getScopeGroupId();

        ArrayList<Question> questions = new ArrayList<>(QuestionLocalServiceUtil.getQuestions(groupID));
        ArrayList<Question> questionsSortedByRating = new ArrayList<>(getQuestionsSortedByRating(serviceContext));

        getQuestionsFilteredByTag(renderRequest);
        getQuestionsFilteredByCategory(renderRequest);

        List<AssetCategory> categories = questions.stream()
                .flatMap(q -> q.safeGetCategories().stream())
                .distinct()
                .collect(toList());
        List<AssetTag> tags = AssetTagLocalServiceUtil.getTags();

        if (!questions.isEmpty()) {
            renderRequest.setAttribute("questions", questions);
            renderRequest.setAttribute("questionsSortedByRating",  questionsSortedByRating);
        }
        if (!categories.isEmpty()) {
            renderRequest.setAttribute("categories",  categories);
        }
        if (!tags.isEmpty()) {
            renderRequest.setAttribute("tags", tags);
        }

        log.info(categories.size() + " categories and " + tags.size() + " tags");
        log.info(questions.size() + " questions and  " + questionsSortedByRating.size()
                + " sorted questions have been passed to renderRequest");

        hideDefaultSuccessMessage(renderRequest);

        try {
            super.render(renderRequest, renderResponse);
        } catch (IOException e) {
            log.error("Portlet can not fulfill this request");
        } catch (PortletException e) {
            log.error("Fatal exception while processing render()");
        }
    }

    @Override
    public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {
        String isQuestionToProcedure = renderRequest.getParameter("oper");

        if(isQuestionToProcedure != null && isQuestionToProcedure.equals("crqp")) {
            include("/editQuestion.jsp", renderRequest, renderResponse);
        }
        else {
            include("/view.jsp", renderRequest, renderResponse);
        }
    }

    private static ServiceContext getServiceContext(PortletRequest request, String className) {
        try {
            return ServiceContextFactory.getInstance(className, request);
        } catch (PortalException e) {
            log.error(e);
        }
        return new ServiceContext();
    }

    private void handleError(ActionRequest request, Exception e, String msg) {
        SessionErrors.add(request, e.getClass().getSimpleName());
        hideDefaultErrorMessage(request);
        log.error(msg);
        log.error(e);
    }

    private void redirectToRedirectUrl(ActionRequest request, ActionResponse response) {
        try {
            String redirectUrl = ParamUtil.getString(request, "redirectURL");
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            log.error("IO Exception during redirect");
            log.error(e);
        }
    }

    // ### PERMISSIONS ###

    public static boolean questionPermissionContains(PermissionChecker permissionChecker, Question question,
                                                     String actionId) {
        String QUESTION_CLASS_NAME = Question.class.getName();
        final long QUESTION_ID = question.getQuestionID();
        final long GROUP_ID = question.getGroupId();

        boolean userHasPermission = permissionChecker.hasPermission(GROUP_ID, QUESTION_CLASS_NAME, QUESTION_ID,
                actionId);
        boolean ownerHasPermission = permissionChecker.hasOwnerPermission(question.getCompanyId(), QUESTION_CLASS_NAME,
                QUESTION_ID, question.getUserId(), actionId);

        //log.info(actionId + " has site permission: " + userHasPermission);
        //log.info(actionId + " has owner permission: " + ownerHasPermission);

        return ownerHasPermission || userHasPermission;
    }

    public static boolean answerPermissionContains(PermissionChecker permissionChecker, Answer answer,
                                                   String actionId) {
        String ANSWER_CLASS_NAME = Answer.class.getName();
        final long ANSWER_ID = answer.getAnswerID();
        final long GROUP_ID = answer.getGroupId();

        return permissionChecker.hasOwnerPermission(answer.getCompanyId(), ANSWER_CLASS_NAME, ANSWER_ID,
                answer.getUserId(), actionId)
                || permissionChecker.hasPermission(GROUP_ID, ANSWER_CLASS_NAME, ANSWER_ID, actionId);
    }

    // #### Testing ####

    public void testSortByRating(ActionRequest request, ActionResponse response) {
        ServiceContext serviceContext = getServiceContext(request, Question.class.getName());
        getQuestionsSortedByRating(serviceContext).forEach(question -> {
            try {
                log.info(question.getTitle());
            } catch (PortalException e) {
                log.error(e);
            }
        });
    }

    private void testNewAnswer(ActionRequest request, ActionResponse response, long questionId, String text) {
        ServiceContext serviceContext = getServiceContext(request, Answer.class.getName());
        AnswerLocalServiceUtil.addAnswer(text, questionId, serviceContext);
        SessionMessages.add(request, "answerAdded");
    }

    public void testNewQuestion(ActionRequest request, ActionResponse response){
        String title = "Toller Titel";
        String text = "Einzigartiger Text";
        ServiceContext serviceContext = getServiceContext(request, Question.class.getName());
        try {
            QuestionLocalServiceUtil.addQuestion(title, text, false, 0, serviceContext);
            SessionMessages.add(request, "questionAdded");
        } catch (Exception e) {
            SessionErrors.add(request, e.getClass().getName());
            PortalUtil.copyRequestParameters(request, response);
            response.setRenderParameter("mvcPath", "/view.jsp");
            log.error(e);
        }

    }

    public void testAnswer(ActionRequest request, ActionResponse response) {
        try {
            ServiceContext serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);
            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            Question question = questions.get(0);
            Question question2 = questions.get(1);
            String text = "Dies ist eine tolle Antwort auf die erste Frage";
            String text2 = "Dies ist eine tolle Antwort auf die zweite Frage";
            testNewAnswer(request, response, question.getQuestionID(), text);
            testNewAnswer(request, response, question2.getQuestionID(), text2);


            List<Answer> answers = AnswerLocalServiceUtil.getAnswersForQuestion(question.getQuestionID());
            log.info("Erste Frage:");
            for (Answer answer : answers) {
                log.info(answer.getText());
            }

            List<Answer> answers2 = AnswerLocalServiceUtil.getAnswersForQuestion(question2.getQuestionID());
            log.info("Zweite Frage:");
            for (Answer answer : answers) {
                log.info(answer.getText());
            }

        } catch (PortalException e) {
            log.error(e);
        }

    }

    public void testDisplayCorrectAnswer(ActionRequest request, ActionResponse response) {
        try {
            ServiceContext serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);

            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            Question question = questions.get(0);

            if (!question.getIsAnswered()) {
                log.info("Not answered yet!");
            } else {
                Answer correctAnswer = question.getCorrectAnswer();

                log.info("Correct Answer: "
                        + correctAnswer.getAnswerID()
                        + " --- Text "
                        + correctAnswer.getText()
                        + " by "
                        + correctAnswer.getUserName());
            }
        } catch (PortalException e) {
            log.error(e);
        }
    }

    public void testDeleteAnswer(ActionRequest request, ActionResponse response) {
        ServiceContext serviceContext;
        try {
            serviceContext = ServiceContextFactory.getInstance(Answer.class.getName(), request);

            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            Question question = questions.get(0);

            List<Answer> answers = AnswerLocalServiceUtil.getAnswersForQuestion(question.getQuestionID());
            log.info("Vor dem löschen");
            for (Answer answer : answers) {
                log.info(answer.getAnswerID() + ": " + answer.getText());
            }
            System.out.print("Asset Count: ");
            testDisplayAssetCount(request, response);

            Answer answer = answers.get(0);
            AnswerLocalServiceUtil.deleteAnswer(answer.getAnswerID(), serviceContext);

            answers = AnswerLocalServiceUtil.getAnswersForQuestion(question.getQuestionID());
            log.info("Nach dem löschen");
            for (Answer editedAnswer : answers) {
                log.info(answer.getAnswerID() + ": " + answer.getText());
            }
            log.info("Asset Count");
            testDisplayAssetCount(request, response);

        } catch (PortalException e) {
            log.error(e);
        }
    }

    public void testDeleteQuestion(ActionRequest request, ActionResponse response) {
        ServiceContext serviceContext;
        try {
            serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);

            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            log.info("Vor dem löschen:");
            for (Question question : questions) {
                log.info(question.getTitle());
            }
            System.out.print("Asset Count: ");
            testDisplayAssetCount(request, response);

            Question toDelete = questions.get(0);
            QuestionLocalServiceUtil.deleteQuestion(toDelete.getQuestionID(), serviceContext);

            questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            log.info("Nach dem löschen");
            for (Question question : questions) {
                log.info(question.getTitle());
            }
            System.out.print("Asset Count: ");
            testDisplayAssetCount(request, response);

        } catch (PortalException e) {
            log.error(e);
        }
    }

    public void testEditQuestion(ActionRequest request, ActionResponse response) {
        ServiceContext serviceContext;
        try {
            serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);
            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());

            Question question = questions.get(0);
            log.info("Vor dem Edit: " + question.getTitle() + ": " + question.getText() + " von " +
                    question.getEditedBy());

            QuestionLocalServiceUtil.editQuestion(question.getQuestionID(), "Ganz neuer Titel", "Ganz neuer Text",
                    serviceContext);

            questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            question = questions.get(0);

            log.info("Nach dem Edit: " + question.getTitle() + ": " + question.getText() + " von " +
                    question.getEditedBy());

        } catch (PortalException e) {
            log.error(e);
        }
    }

    public void testEditAnswer(ActionRequest request, ActionResponse response) {
        ServiceContext serviceContext;
        try {
            serviceContext = ServiceContextFactory.getInstance(Answer.class.getName(), request);
            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            Question question = questions.get(0);
            List<Answer> answers = AnswerLocalServiceUtil.getAnswersForQuestion(question.getQuestionID());
            Answer answer = answers.get(0);

            log.info("Vor dem edit: " + answer.getText() + " editiert am " + answer.getModifiedDate());

            AnswerLocalServiceUtil.editAnswer(answer.getAnswerID(), "Ganz neue Antwort", serviceContext);

            answers = AnswerLocalServiceUtil.getAnswersForQuestion(question.getQuestionID());
            answer = answers.get(0);

            log.info("Nach dem edit: " + answer.getText() + " editiert am " + answer.getModifiedDate());

        } catch (PortalException e) {
            log.error(e);
        }
    }

    public void testDisplayAssetCount(ActionRequest request, ActionResponse response) {
        log.info(AssetEntryLocalServiceUtil.getAssetEntriesCount());
    }

    public void testDisplayAssets(ActionRequest request, ActionResponse response) throws PortalException {
        List<AssetEntry> entries = AssetEntryLocalServiceUtil.getAssetEntries(0, AssetEntryLocalServiceUtil.getAssetEntriesCount());
        for (AssetEntry entry : entries) {

            if (entry.getClassName().equals(Question.class.getName())) {
                Question question = QuestionLocalServiceUtil.getQuestion(entry.getClassPK());
                log.info("Class PK: " + entry.getClassPK()
                        + " --- Class Name ID: " + entry.getClassNameId()
                        + " --- Title: " + entry.getTitle()
                        + " --- Text: " + entry.getDescription()
                        + " --- isAnswered: " + question.getIsAnswered()
                        + " --- correct answer ID: " + question.getCorrectAnswerId());
            } else if (entry.getClassName().equals(Answer.class.getName())) {
                try {
                    log.info("Class PK: " + entry.getClassPK()
                            + " --- Class Name ID: " + entry.getClassNameId()
                            + " --- Text: " + entry.getDescription()
                            + " to Question " + AnswerLocalServiceUtil.getAnswer(entry.getClassPK()).getQuestionId());
                } catch (PortalException e) {
                    log.error(e);
                }
            }
        }
    }

    public void testDeleteAssets(ActionRequest request, ActionResponse response) throws PortalException {

        ServiceContext serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);
        long groupID = serviceContext.getScopeGroupId();
        List<Question> questions = QuestionLocalServiceUtil.getQuestions(groupID);

        questions.forEach(question -> QuestionLocalServiceUtil.deleteQuestion(question.getQuestionID(),
                serviceContext));
    }

    public void testPrintCategoryIdsOfTheFirstQuestion(ActionRequest request, ActionResponse response) {
        ServiceContext serviceContext;
        try {
            serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);
            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());

            Question question = questions.get(0);
            long[] categoryIds = question.getCategoryIds();
            for (long id : categoryIds) {
                log.info(id);
            }
        } catch (PortalException e) {
            log.error(e);
        }
    }

    public void testFilterQuestionsByCategory(ActionRequest request, ActionResponse response) {
        ServiceContext serviceContext;
        try {
            serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);
            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            long idToFilter = 36181L;

            List<Question> filteredQuestions = questions.stream()
                    .filter(question -> filterByCategoryId(question, idToFilter))
                    .collect(toList());

            log.info("Gefilterte Fragen: ");
            filteredQuestions.forEach(question -> log.info(question.safeGetTitle()));

        } catch (PortalException e) {
            log.error(e);
        }
    }

    public void testFilterQuestionsByTag(ActionRequest request, ActionResponse response) {
        ServiceContext serviceContext;
        try {
            serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);
            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            String tagToFilter = "new";

            List<Question> filteredQuestions = questions.stream()
                    .filter(question -> filterByTagName(question, tagToFilter))
                    .collect(toList());

            log.info("Gefilterte Fragen: ");
            filteredQuestions.forEach(question -> log.info(question.safeGetTitle()));

        } catch (PortalException e) {
            log.error(e);
        }
    }
}