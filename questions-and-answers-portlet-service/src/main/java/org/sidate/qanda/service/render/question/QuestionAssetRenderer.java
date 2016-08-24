package org.sidate.qanda.service.render.question;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import org.sidate.qanda.model.Question;
import org.sidate.qanda.model.impl.QuestionImpl;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;
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
        return null;
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

    public  String getURLViewInContext(LiferayPortletRequest liferayPortletRequest,
                                       LiferayPortletResponse liferayPortletResponse,
                                       String noSuchEntryRedirect) throws Exception {

        String portletId = question.getPortletId();
        PortletURL backURL = liferayPortletResponse.createRenderURL(portletId);
        backURL.setParameter("mvcPath", "/view.jsp");

        PortletURL url = liferayPortletResponse.createRenderURL(portletId);
        url.setParameter("mvcPath", "/showQuestion.jsp");
        url.setParameter("backURL", backURL.toString());
        url.setParameter("questionID", String.valueOf(question.getQuestionID()));

        return url.toString();
    }
}
