package org.sidate.questions_and_answers.portlet;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import org.sidate.questions_and_answers.model.Answer;
import org.sidate.questions_and_answers.model.Question;
import org.sidate.questions_and_answers.service.AnswerLocalServiceUtil;
import org.sidate.questions_and_answers.service.QuestionLocalServiceUtil;
import org.osgi.service.component.annotations.Component;

import javax.portlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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

    private static Log log = LogFactoryUtil.getLog(QuestionsAndAnswersPortlet.class);


    // #### Question ####


    public void newQuestion(ActionRequest request, ActionResponse response)
            throws PortalException, SystemException {

        ServiceContext serviceContext = ServiceContextFactory.getInstance(
                Question.class.getName(), request);

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
            log.error(e.getClass().getName() + "\n" + e.getMessage());
        }

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
            QuestionLocalServiceUtil.deleteQuestion(questionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the correct answer ID for the given question ID.
     * This method can also be used to unset a correct answer by passing 0 as the answer ID.
     * @throws PortalException
     */
    public void setCorrectAnswer(ActionRequest request, ActionResponse response, long questionId, long answerId) throws PortalException {
        ServiceContext serviceContext = ServiceContextFactory.getInstance(Answer.class.getName(), request);
        QuestionLocalServiceUtil.setCorrectAnswer(answerId, questionId, serviceContext);
        SessionMessages.add(request, "answerAccepted");
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

            if (!questions.isEmpty()) {
                renderRequest.setAttribute("questions", questions);
            }

            super.render(renderRequest, renderResponse);
        } catch (PortalException | PortletException e) {
            log.error(e.getClass().getName() + "\n" + e.getMessage());
        }

    }


    // #### Testing ####


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

    public void testAnswer(ActionRequest request, ActionResponse response){
        try {
            ServiceContext serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);
            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            Question question = questions.get(0);
            Question question2 = questions.get(1);
            String text = "Dies ist eine tolle Antwort! auf die erste Frage";
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

    // Sets and gets the correct answer
    public void testCorrectAnswer(ActionRequest request, ActionResponse response){
        try {
            ServiceContext serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);

            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            Question question = questions.get(0);
            List<Answer> answers = AnswerLocalServiceUtil.getAnswersForQuestion(question.getQuestionID());
            Answer answer = answers.get(0);

            setCorrectAnswer(request,response, question.getQuestionID(), answer.getAnswerID());

            Answer correctAnswer = AnswerLocalServiceUtil.getAnswer(question.getCorrectAnswerId());
            System.out.println("Correct Answer");
            System.out.println(correctAnswer.getText());
        } catch (PortalException e) {
            e.printStackTrace();
        }

    }

    public void testDeleteAnswer(ActionRequest request, ActionResponse response){
        ServiceContext serviceContext = null;
        try {
            serviceContext = ServiceContextFactory.getInstance(Answer.class.getName(), request);

            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            Question question = questions.get(0);

            List<Answer> answers = AnswerLocalServiceUtil.getAnswersForQuestion(question.getQuestionID());
            System.out.println("Vor dem löschen");
            for (Answer answer:answers) {
                System.out.println(answer.getText());
            }
            System.out.print("Asset Count: ");
            testDisplayAssetCount(request, response);

            Answer answer = answers.get(0);
            AnswerLocalServiceUtil.deleteAnswer(answer);

            answers = AnswerLocalServiceUtil.getAnswersForQuestion(question.getQuestionID());
            System.out.println("Nach dem löschen");
            for (Answer editedAnswer:answers) {
                System.out.println(editedAnswer.getText());
            }
            System.out.println("Asset Count");
            testDisplayAssetCount(request, response);

        } catch (PortalException e) {
            e.printStackTrace();
        }
    }

    public void testDeleteQuestion(ActionRequest request, ActionResponse response){
        ServiceContext serviceContext = null;
        try {
            serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);

            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            System.out.println("Vor dem löschen");
            for (Question question : questions) {
                System.out.println(question.getTitle());
            }
            System.out.print("Asset Count: ");
            testDisplayAssetCount(request, response);

            Question toDelete = questions.get(0);
            QuestionLocalServiceUtil.deleteQuestion(toDelete);

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
        ServiceContext serviceContext = null;
        try {
            serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);
            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());

            Question question = questions.get(0);
            System.out.println("Vor dem edit: " + question.getTitle() + ": " + question.getText()+" von " +
                    question.getModifiedBy());

            QuestionLocalServiceUtil.editQuestion(question.getQuestionID(), "Ganz neuer Titel", "Ganz neuer Text",
                    serviceContext);

            questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            question = questions.get(0);

            System.out.println("Nach dem edit: " + question.getTitle() + ": " + question.getText()+" von " +
                    question.getModifiedBy());

        } catch (PortalException e) {
            e.printStackTrace();
        }


    }

    public void testEditAnswer(ActionRequest request, ActionResponse response) {
        ServiceContext serviceContext = null;
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

    public void testDisplayAssets(ActionRequest request, ActionResponse response) {
        List<AssetEntry> entries = AssetEntryLocalServiceUtil.getAssetEntries(0, AssetEntryLocalServiceUtil.getAssetEntriesCount());
        for (AssetEntry entry:entries) {
            System.out.println(entry.getDescription());
        }
    }

}