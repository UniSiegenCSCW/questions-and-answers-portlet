package org.sidate.qanda.service.render.answer;

import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import org.sidate.qanda.model.Answer;
import org.sidate.qanda.model.impl.AnswerImpl;
import org.sidate.qanda.service.QuestionLocalServiceUtil;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
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
        return null;
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

    public  String getURLViewInContext(LiferayPortletRequest liferayPortletRequest,
                                       LiferayPortletResponse liferayPortletResponse,
                                       String noSuchEntryRedirect) throws Exception {

        String portletId = answer.getPortletId();
        PortletURL backURL = liferayPortletResponse.createRenderURL(portletId);
        backURL.setParameter("mvcPath", "/view.jsp");

        PortletURL url = liferayPortletResponse.createRenderURL(portletId);
        url.setParameter("mvcPath", "/showQuestion.jsp");
        url.setParameter("backURL", backURL.toString());
        url.setParameter("questionID", String.valueOf(answer.getQuestionId()));

        return url.toString();
    }
}
