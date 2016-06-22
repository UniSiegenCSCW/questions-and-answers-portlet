package de.sidate.questions_and_answers.portlet;

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
import de.sidate.questions_and_answers.model.Answer;
import de.sidate.questions_and_answers.model.Question;
import de.sidate.questions_and_answers.service.AnswerLocalServiceUtil;
import de.sidate.questions_and_answers.service.QuestionLocalServiceUtil;
import de.sidate.questions_and_answers.service.persistence.QuestionPersistence;
import de.sidate.questions_and_answers.service.persistence.impl.QuestionPersistenceImpl;
import org.osgi.service.component.annotations.Component;

import javax.portlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;


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

	public void newQuestion(ActionRequest request, ActionResponse response)
			throws PortalException, SystemException {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
				Question.class.getName(), request);

		String title = ParamUtil.getString(request, "title");
		String text = ParamUtil.getString(request, "text");

		try {
            QuestionLocalServiceUtil.addQuestion(serviceContext.getUserId(), title, text, serviceContext);
            SessionMessages.add(request, "questionAdded");
        }
        catch (Exception e) {
			SessionErrors.add(request, e.getClass().getName());
			PortalUtil.copyRequestParameters(request, response);
			response.setRenderParameter("mvcPath", "/view.jsp");
            log.error(e.getClass().getName() + "\n" + e.getMessage());
		}

	}

    public void newAnswer(ActionRequest request, ActionResponse response, long questionId, String text)
            throws PortalException, SystemException {

        ServiceContext serviceContext = ServiceContextFactory.getInstance(
                Answer.class.getName(), request);

        //String text = ParamUtil.getString(request, "text");

        try {
            AnswerLocalServiceUtil.addAnswer(serviceContext.getUserId(), text, questionId, serviceContext);
            SessionMessages.add(request, "answerAdded");
        }
        catch (Exception e) {
            SessionErrors.add(request, e.getClass().getName());
            PortalUtil.copyRequestParameters(request, response);
            response.setRenderParameter("mvcPath", "/view.jsp");
            log.error(e.getClass().getName() + "\n" + e.getMessage());
        }

    }

    public void acceptAnswer(ActionRequest request, ActionResponse response, long questionId, long answerId) {
        QuestionLocalServiceUtil.setCorrectAnswer(answerId, questionId);
        SessionMessages.add(request, "answerAccepted");

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




    public void testAnswer(ActionRequest request, ActionResponse response){
        try {
            ServiceContext serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);
            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            Question question = questions.get(0);
            Question question2 = questions.get(1);
            String text = "Dies ist eine tolle Antwort! auf die erste Frage";
            String text2 = "Dies ist eine tolle Antwort auf die zweite Frage";
            newAnswer(request, response, question.getQuestionID(), text);
            newAnswer(request, response, question2.getQuestionID(), text2);


            List<Answer> answers = AnswerLocalServiceUtil.getAnswersForQuestion(question.getQuestionID());
            System.out.println("Erste Frage:");
            answers.forEach(answer -> System.out.println(answer.getText()));

            List<Answer> answers2 = AnswerLocalServiceUtil.getAnswersForQuestion(question2.getQuestionID());
            System.out.println("Zweit Frage:");
            answers2.forEach(answer -> System.out.println(answer.getText()));

        } catch (PortalException e) {
            e.printStackTrace();
        }

    }

    public void testCorrectAnswer(ActionRequest request, ActionResponse response){
        try {
            ServiceContext serviceContext = ServiceContextFactory.getInstance(Question.class.getName(), request);

            List<Question> questions = QuestionLocalServiceUtil.getQuestions(serviceContext.getScopeGroupId());
            Question question = questions.get(0);
            //List<Answer> answers = AnswerLocalServiceUtil.getAnswersForQuestion(question.getQuestionID());
            //Answer answer = answers.get(0);

            Answer answer = AnswerLocalServiceUtil.addAnswer(serviceContext.getUserId(), "Korrekte Antwort", question.getQuestionID(), serviceContext);
            acceptAnswer(request,response, question.getQuestionID(), answer.getAnswerID());

            Answer correctAnswer = AnswerLocalServiceUtil.getAnswer(question.getCorrectAnswerId());
            System.out.println("Correct Answer");
            System.out.println(correctAnswer.getText());
        } catch (PortalException e) {
            e.printStackTrace();
        }

    }

}