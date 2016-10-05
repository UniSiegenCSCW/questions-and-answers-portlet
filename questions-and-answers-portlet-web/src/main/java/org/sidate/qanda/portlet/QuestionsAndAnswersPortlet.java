package org.sidate.qanda.portlet;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryModel;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import org.osgi.service.component.annotations.Component;
import org.sidate.qanda.model.Answer;
import org.sidate.qanda.model.Question;
import org.sidate.qanda.service.AnswerLocalServiceUtil;
import org.sidate.qanda.service.QuestionLocalServiceUtil;

import javax.portlet.*;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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


    public void newQuestion(ActionRequest request, ActionResponse response)
            throws PortalException, SystemException {

        ServiceContext serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);

        String title = ParamUtil.getString(request, "title");
        String text = ParamUtil.getString(request, "text");

        try {
            QuestionLocalServiceUtil.addQuestion(title, text, serviceContext);
            SessionMessages.add(request, "questionAdded");
        }
        catch (Exception e) {
            SessionErrors.add(request, e.getClass().getName());
            PortalUtil.copyRequestParameters(request, response);
            response.setRenderParameter("mvcPath", "/view.jsp");
            log.error(e);
        }
    }

    /**
     * Sets the correct answer ID, may also be used to unset the correct answer ID by
     * passing 0 as the ID parameter.
     */
    public void setCorrectAnswer(ActionRequest request, ActionResponse response) throws PortalException {
        long answerId = ParamUtil.getLong(request, "answerID");
        Answer answer = AnswerLocalServiceUtil.getAnswer(answerId);
        long questionId = answer.getQuestionId();
        Question question = QuestionLocalServiceUtil.getQuestion(questionId);
        question.setCorrectAnswer(answerId);

        SessionMessages.add(request, "answerAccepted");
        log.info("Answer " + answerId + " has been accepted");
    }

    public void unsetCorrectAnswer(ActionRequest request, ActionResponse response) throws PortalException {
        long questionId = ParamUtil.getLong(request, "questionID");
        Question question = QuestionLocalServiceUtil.getQuestion(questionId);
        question.unsetCorrectAnswer();

        SessionMessages.add(request, "answerUnset");
        log.info("Answer for question " + questionId + " has been unset.");
    }

    public void editQuestion(ActionRequest request, ActionResponse response) throws PortalException {
        String title = ParamUtil.getString(request, "title");
        String text = ParamUtil.getString(request, "text");
        long questionId = ParamUtil.getLong(request, "questionID");
        ServiceContext serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);

        QuestionLocalServiceUtil.editQuestion(questionId, title, text, serviceContext);
    }

    public void deleteQuestion(ActionRequest request, ActionResponse response){
        long questionId = ParamUtil.getLong(request, "questionID");

        try {
            ServiceContext serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);
            QuestionLocalServiceUtil.deleteQuestion(questionId, serviceContext);
        } catch (Exception e) {
            SessionErrors.add(request, e.getClass().getName());
            //PortalUtil.copyRequestParameters(request, response); ???
            response.setRenderParameter("mvcPath", "/view.jsp");
            log.error("Error on getInstance from ServiceContextFactory!");
        }

    }

    /**
     * Returns the questions with a specified AssetCategory name passed via ParamUtil. This method first searches for
     * the AssetCategory with the given name and if exists, filters the questions by that ID.
     *
     * Annotation: This method allows querying the questions by category name, not ID! It depends on the GUI layout
     * whether the ID or name will be used.
     */
    public void getQuestionsFilteredByCategory(ActionRequest request, ActionResponse response){

        ServiceContext serviceContext;

        try {
            serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);
            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            String categoryName = ParamUtil.getString(request, "category");
            long categoryId = getCategoryIdByName(categoryName);

            List<Question> filteredQuestions = questions.stream()
                    .filter(question -> filterByCategoryId(question, categoryId))
                    .collect(toList());

            request.setAttribute("questionsFilteredByCategory", filteredQuestions);

        } catch (PortalException e) {
            SessionErrors.add(request, e.getClass().getName());
            PortalUtil.copyRequestParameters(request, response);
            response.setRenderParameter("mvcPath", "/view.jsp");
            log.error(e.getClass().getName() + "\n" + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("You supplied a category name which does not seem to have a corresponding category!");
        }

    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private long getCategoryIdByName(String categoryName) throws NoSuchElementException {
        List<AssetCategory> categories = AssetCategoryLocalServiceUtil.getCategories();

        return categories.parallelStream()
                    .filter(category -> category.getName().equals(categoryName))
                    .mapToLong(AssetCategoryModel::getCategoryId)
                    .findFirst().getAsLong();
    }

    /**
     * Returns the questions with a specified Tag name passed via ParamUtil. This method filters the questions by that
     * tag, so if a non existing tag is supplied it will return an empty List.
     */
    public void getQuestionsFilteredByTag(ActionRequest request, ActionResponse response) {
        ServiceContext serviceContext;
        try {
            serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);
            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            String tagToFilter = ParamUtil.getString(request, "tag");

            List<Question> filteredQuestions = questions.stream()
                    .filter(question -> filterByTagName(question, tagToFilter))
                    .collect(toList());

            request.setAttribute("questionsFilteredByTag", filteredQuestions);


        } catch (PortalException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method returns an ArrayList of questions sorted by their rating. Questions that are older than one month
     * are dismissed.
     * @return An ArrayList of sorted questions.
     */
    private ArrayList<Question> getQuestionsSortedByRating(ServiceContext serviceContext) {
        List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
        Comparator<Question> byRating = (questionOne, questionTwo) -> Double.compare(questionOne.getRating(),
                questionTwo.getRating());

        return (ArrayList<Question>) questions.stream()
                                            .filter(isRecent())
                                            .sorted(byRating.reversed())
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


    private String safeGetTitle(Question question) {
        try {
            return question.getTitle();
        } catch (PortalException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean filterByCategoryId(Question question, long idToFilter) {
        return LongStream.of(question.getCategoryIds())
                .anyMatch(categoryId -> categoryId == idToFilter);
    }

    private boolean filterByTagName(Question question, String tagToFilter) {
        return Arrays.stream(question.getTagNames())
                .anyMatch(tag -> tag.equals(tagToFilter));
    }

    // #### Answers ####


    public void newAnswer(ActionRequest request, ActionResponse response)
            throws PortalException, SystemException {

        ServiceContext serviceContext = ServiceContextFactory.getInstance(
                Answer.class.getName(), request);

        String text = ParamUtil.getString(request, "text");
        String redirectUrl = ParamUtil.getString(request, "redirectURL");
        long questionId = ParamUtil.getLong(request, "questionID");

        try {
            AnswerLocalServiceUtil.addAnswer(text, questionId, serviceContext);
            SessionMessages.add(request, "answerAdded");
            response.sendRedirect(redirectUrl);
        }
        catch (Exception e) {
            SessionErrors.add(request, e.getClass().getName());
            PortalUtil.copyRequestParameters(request, response);
            response.setRenderParameter("mvcPath", "/view.jsp");
            log.error(e.getClass().getName() + "\n" + e.getMessage());
        }

    }

    public void editAnswer(ActionRequest request, ActionResponse response) throws PortalException {
        ServiceContext serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);
        long answerId = ParamUtil.getLong(request, "answerID");
        String text = ParamUtil.getString(request, "text");
        String redirectUrl = ParamUtil.getString(request, "redirectURL");
        try {
            AnswerLocalServiceUtil.editAnswer(answerId, text, serviceContext);
            response.sendRedirect(redirectUrl);
        }
        catch (Exception e) {
            SessionErrors.add(request, e.getClass().getName());
            PortalUtil.copyRequestParameters(request, response);
            response.setRenderParameter("mvcPath", "/view.jsp");
            log.error(e.getClass().getName() + "\n" + e.getMessage());
        }
    }


    public void deleteAnswer(ActionRequest request, ActionResponse response) throws PortalException{
        ServiceContext serviceContext = ServiceContextFactory.getInstance(Answer.class.getName(), request);
        long answerId = ParamUtil.getLong(request, "answerID");
        String redirectUrl = ParamUtil.getString(request, "redirectURL");
        try {
            AnswerLocalServiceUtil.deleteAnswer(answerId, serviceContext);
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException {

        try {
            ServiceContext serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), renderRequest);
            long groupID = serviceContext.getScopeGroupId();

            ArrayList<Question> questions = new ArrayList<>(QuestionLocalServiceUtil.getQuestions(groupID));
            ArrayList<Question> questionsSortedByRating = getQuestionsSortedByRating(serviceContext);

            if (!questions.isEmpty()) {
                renderRequest.setAttribute("questions", questions);
                renderRequest.setAttribute("questionsSortedByRating", questionsSortedByRating);
            }
            log.info(questions.size() + " questions and  " + questionsSortedByRating.size()
                    + " sorted questions have been passed to renderRequest");

            super.render(renderRequest, renderResponse);
        } catch (PortletException e) {
            log.error("A Portlet Error has been thrown by render()");
            e.printStackTrace();
        } catch (PortalException e) {
            log.error("A Portal Error has been thrown by render()");
            e.printStackTrace();
        }
    }


    // #### Testing ####

    public void testSortByRating(ActionRequest request, ActionResponse response) {
        try {
            ServiceContext serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);
            getQuestionsSortedByRating(serviceContext).forEach(question -> {
                try {
                    System.out.println(question.getTitle());
                } catch (PortalException e) {
                    e.printStackTrace();
                }
            });
        } catch (PortalException e) {
            e.printStackTrace();
        }
    }

    private void testNewAnswer(ActionRequest request, ActionResponse response, long questionId, String text)
            throws PortalException, SystemException {

        ServiceContext serviceContext = ServiceContextFactory.getInstance(
                Answer.class.getName(), request);

        try {
            AnswerLocalServiceUtil.addAnswer(text, questionId, serviceContext);
            SessionMessages.add(request, "answerAdded");
        }
        catch (Exception e) {
            SessionErrors.add(request, e.getClass().getName());
            PortalUtil.copyRequestParameters(request, response);
            response.setRenderParameter("mvcPath", "/view.jsp");
            log.error(e.getClass().getName() + "\n" + e.getMessage());
        }

    }

    public void testNewQuestion(ActionRequest request, ActionResponse response){
        String title = "Toller Titel";
        String text = "Einzigartiger Text";

        try {
            ServiceContext serviceContext = ServiceContextFactory.getInstance(
                Question.class.getName(), request);
            QuestionLocalServiceUtil.addQuestion(title, text, serviceContext);
            SessionMessages.add(request, "questionAdded");
        }
        catch (Exception e) {
            SessionErrors.add(request, e.getClass().getName());
            PortalUtil.copyRequestParameters(request, response);
            response.setRenderParameter("mvcPath", "/view.jsp");
            log.error(e.getClass().getName() + "\n" + e.getMessage());
        }

    }

    public void testAnswer(ActionRequest request, ActionResponse response){
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
            System.out.println("Erste Frage:");
            for (Answer answer:answers) {
                System.out.println(answer.getText());
            }

            List<Answer> answers2 = AnswerLocalServiceUtil.getAnswersForQuestion(question2.getQuestionID());
            System.out.println("Zweite Frage:");
            for (Answer answer:answers) {
                System.out.println(answer.getText());
            }

        } catch (PortalException e) {
            e.printStackTrace();
        }

    }

    public void testDisplayCorrectAnswer(ActionRequest request, ActionResponse response){
        try {
            ServiceContext serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);

            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            Question question = questions.get(0);

            if (!question.getIsAnswered()) {
                System.out.println("Not answered yet!");
            } else {
                Answer correctAnswer = question.getCorrectAnswer();

                System.out.println("Correct Answer: "
                        + correctAnswer.getAnswerID()
                        + " --- Text "
                        + correctAnswer.getText()
                        + " by "
                        + correctAnswer.getUserName());
            }
        } catch (PortalException e) {
            e.printStackTrace();
        }
    }

    public void testDeleteAnswer(ActionRequest request, ActionResponse response){
        ServiceContext serviceContext;
        try {
            serviceContext = ServiceContextFactory.getInstance(Answer.class.getName(), request);

            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            Question question = questions.get(0);

            List<Answer> answers = AnswerLocalServiceUtil.getAnswersForQuestion(question.getQuestionID());
            System.out.println("Vor dem löschen");
            for (Answer answer:answers) {
                System.out.println(answer.getAnswerID() + ": " + answer.getText());
            }
            System.out.print("Asset Count: ");
            testDisplayAssetCount(request, response);

            Answer answer = answers.get(0);
            AnswerLocalServiceUtil.deleteAnswer(answer.getAnswerID(), serviceContext);

            answers = AnswerLocalServiceUtil.getAnswersForQuestion(question.getQuestionID());
            System.out.println("Nach dem löschen");
            for (Answer editedAnswer:answers) {
                System.out.println(answer.getAnswerID() + ": " + answer.getText());
            }
            System.out.println("Asset Count");
            testDisplayAssetCount(request, response);

        } catch (PortalException e) {
            e.printStackTrace();
        }
    }

    public void testDeleteQuestion(ActionRequest request, ActionResponse response){
        ServiceContext serviceContext;
        try {
            serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);

            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            System.out.println("Vor dem löschen:");
            for (Question question : questions) {
                System.out.println(question.getTitle());
            }
            System.out.print("Asset Count: ");
            testDisplayAssetCount(request, response);

            Question toDelete = questions.get(0);
            QuestionLocalServiceUtil.deleteQuestion(toDelete.getQuestionID(), serviceContext);

            questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            System.out.println("Nach dem löschen");
            for (Question question : questions) {
                System.out.println(question.getTitle());
            }
            System.out.print("Asset Count: ");
            testDisplayAssetCount(request, response);

        } catch (PortalException e) {
            e.printStackTrace();
        }
    }

    public void testEditQuestion(ActionRequest request, ActionResponse response) {
        ServiceContext serviceContext;
        try {
            serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);
            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());

            Question question = questions.get(0);
            System.out.println("Vor dem Edit: " + question.getTitle() + ": " + question.getText()+" von " +
                    question.getEditedBy());

            QuestionLocalServiceUtil.editQuestion(question.getQuestionID(), "Ganz neuer Titel", "Ganz neuer Text",
                    serviceContext);

            questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            question = questions.get(0);

            System.out.println("Nach dem Edit: " + question.getTitle() + ": " + question.getText()+" von " +
                    question.getEditedBy());

        } catch (PortalException e) {
            e.printStackTrace();
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

            System.out.println("Vor dem edit: " + answer.getText() + " editiert am " + answer.getModifiedDate());

            AnswerLocalServiceUtil.editAnswer(answer.getAnswerID(), "Ganz neue Antwort", serviceContext);

            answers = AnswerLocalServiceUtil.getAnswersForQuestion(question.getQuestionID());
            answer = answers.get(0);

            System.out.println("Nach dem edit: " + answer.getText() + " editiert am " + answer.getModifiedDate());

        } catch (PortalException e) {
            e.printStackTrace();
        }
    }

    public void testDisplayAssetCount(ActionRequest request, ActionResponse response) {
        System.out.println(AssetEntryLocalServiceUtil.getAssetEntriesCount());
    }

    public void testDisplayAssets(ActionRequest request, ActionResponse response) throws PortalException {
        List<AssetEntry> entries = AssetEntryLocalServiceUtil.getAssetEntries(0, AssetEntryLocalServiceUtil.getAssetEntriesCount());
        for (AssetEntry entry : entries) {

            if (entry.getClassName().equals(Question.class.getName())) {
                Question question = QuestionLocalServiceUtil.getQuestion(entry.getClassPK());
                System.out.println("Class PK: " + entry.getClassPK()
                        + " --- Class Name ID: " + entry.getClassNameId()
                        + " --- Title: " + entry.getTitle()
                        + " --- Text: " + entry.getDescription()
                        + " --- isAnswered: " + question.getIsAnswered()
                        + " --- correct answer ID: " + question.getCorrectAnswerId());
            }
            else if (entry.getClassName().equals(Answer.class.getName())) {
                try {
                    System.out.println("Class PK: " + entry.getClassPK()
                            + " --- Class Name ID: " + entry.getClassNameId()
                            + " --- Text: " + entry.getDescription()
                            + " to Question " + AnswerLocalServiceUtil.getAnswer(entry.getClassPK()).getQuestionId());
                } catch (PortalException e) {
                    e.printStackTrace();
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
                System.out.println(id);
            }
        } catch (PortalException e) {
            e.printStackTrace();
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

            System.out.println("Gefilterte Fragen: ");
            filteredQuestions.forEach(question -> System.out.println(safeGetTitle(question)));

        } catch (PortalException e) {
            e.printStackTrace();
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

            System.out.println("Gefilterte Fragen: ");
            filteredQuestions.forEach(question -> System.out.println(safeGetTitle(question)));

        } catch (PortalException e) {
            e.printStackTrace();
        }
    }

}