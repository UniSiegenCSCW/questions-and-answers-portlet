package org.sidate.qanda.service.render.question;

import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.sidate.qanda.model.Question;
import org.sidate.qanda.model.impl.QuestionImpl;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created by adhominem on 27.07.16.
 */

public class QuestionAssetRenderer extends BaseJSPAssetRenderer<Question> {

    private static final Log log = LogFactoryUtil.getLog(QuestionAssetRenderer.class);
    private Question question = null;

    public QuestionAssetRenderer(Question question) {
        this.question = (question == null) ? new QuestionImpl() : question;
    }

    @Override
    public String getJspPath(HttpServletRequest request, String template) {
        return (template.equals(TEMPLATE_ABSTRACT) || template.equals(TEMPLATE_FULL_CONTENT))
                ? "/asset/" + template + ".jsp"
                : null;

    }

    @Override
    public Question getAssetObject() {
        return question;
    }

    @Override
    public long getGroupId() {
        return question.getGroupId();
    }

    @Override
    public long getUserId() {
        return question.getUserId();
    }

    @Override
    public String getUserName() {
        return question.getUserName();
    }

    @Override
    public String getUuid() {
        return question.getUuid();
    }

    @Override
    public String getClassName() {
        return Question.class.getName();
    }

    @Override
    public long getClassPK() {
        return question.getQuestionID();
    }

    @Override
    public String getTitle(Locale locale) {
        String title = null;

        try {
            title = "<b>" + question.getTitle() + "</b>";
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
            summary = "<b>" + question.getTitle() + "</b>";
        } catch (PortalException e) {
            log.error(e.getClass().getName() + " has been thrown in question.getTitle(): \n" + e.getMessage());
            e.printStackTrace();
        }

        return summary;
    }
}
