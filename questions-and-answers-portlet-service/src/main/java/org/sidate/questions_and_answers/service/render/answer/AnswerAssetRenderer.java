package org.sidate.questions_and_answers.service.render.answer;

import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.sidate.questions_and_answers.model.Answer;
import org.sidate.questions_and_answers.model.impl.AnswerImpl;
import org.sidate.questions_and_answers.service.QuestionLocalServiceUtil;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created by User on 05.08.2016.
 */

public class AnswerAssetRenderer  extends BaseJSPAssetRenderer<Answer> {

    private static final Log log = LogFactoryUtil.getLog(AnswerAssetRenderer.class);
    private Answer answer = null;

    public AnswerAssetRenderer(Answer answer){
        this.answer = (answer == null) ? new AnswerImpl() : answer;
    }

    @Override
    public String getJspPath(HttpServletRequest request, String template) {
        return (template.equals(TEMPLATE_ABSTRACT) || template.equals(TEMPLATE_FULL_CONTENT))
                ? "/asset/" + template + ".jsp"
                : null;
    }

    @Override
    public Answer getAssetObject() { return answer; }

    @Override
    public long getGroupId() { return answer.getGroupId(); }

    @Override
    public long getUserId() {
        return answer.getUserId();
    }

    @Override
    public String getUserName() {
        return answer.getUserName();
    }

    @Override
    public String getUuid() {
        return answer.getUuid();
    }

    @Override
    public String getClassName() {
        return Answer.class.getName();
    }

    @Override
    public long getClassPK() {
        return answer.getAnswerID();
    }

    @Override
    public String getTitle(Locale locale) {
        String title = null;

        try {
            title = "<b>" + "Antwort auf " + QuestionLocalServiceUtil.getQuestion(answer.getQuestionId()).getTitle() + "</b>";
        } catch (PortalException e) {
            log.error(e.getClass().getName() + " has been thrown in question.getTitle(): \n" + e.getMessage());
            e.printStackTrace();
        }

        return title;
    }

    @Override
    public String getSummary(PortletRequest portletRequest, PortletResponse portletResponse) {
        String summary = null;

        try {
            summary = "<b>" + "Antwort auf " + QuestionLocalServiceUtil.getQuestion(answer.getQuestionId()).getTitle() + "</b>";
        } catch (PortalException e) {
            log.error(e.getClass().getName() + " has been thrown in question.getTitle(): \n" + e.getMessage());
            e.printStackTrace();
        }

        return summary;
    }
}
