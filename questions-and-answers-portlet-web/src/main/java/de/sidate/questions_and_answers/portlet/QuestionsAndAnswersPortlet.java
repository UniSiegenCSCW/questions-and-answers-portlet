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
import de.sidate.questions_and_answers.model.Question;
import de.sidate.questions_and_answers.service.QuestionLocalServiceUtil;
import org.osgi.service.component.annotations.Component;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import java.util.List;

import static java.util.stream.Collectors.joining;


@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=questions-and-answers-portlet Portlet",
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

            long groupID = serviceContext.getScopeGroupId();
            List<Question> questions = QuestionLocalServiceUtil.getQuestions(groupID);

            // Logging sucessful DB access
            log.info(questions.stream()
                    .map(question -> question.getTitle())
                    .collect(joining(" ")));
        }
        catch (Exception e) {
			SessionErrors.add(request, e.getClass().getName());
			PortalUtil.copyRequestParameters(request, response);
			response.setRenderParameter("mvcPath", "/view.jsp");
            log.error(e.getClass().getName() + "\n" + e.getMessage());
		}

	}
}