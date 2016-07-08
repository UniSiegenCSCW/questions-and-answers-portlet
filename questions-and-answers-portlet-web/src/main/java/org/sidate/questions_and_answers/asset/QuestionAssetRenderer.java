package org.sidate.questions_and_answers.asset;

import com.liferay.asset.kernel.model.BaseAssetRenderer;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.WebKeys;
import org.sidate.questions_and_answers.model.Question;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Created by jk on 08.07.16.
 */

public class QuestionAssetRenderer extends BaseJSPAssetRenderer {

    private Question question;

    public QuestionAssetRenderer(Question question) {
        this.question = question;
    }

    // EDIT AND VIEW PERMISSIONS MUST BE INSERTED HERE


    @Override
    public Object getAssetObject() {
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
    public String getSummary(PortletRequest portletRequest, PortletResponse portletResponse) {
        return "THIS SHOULD BE A SUMMARY FOR QUESTION_ASSET_RENDERER";
    }

    @Override
    public String getTitle(Locale locale) {
        try {
            return question.getTitle();
        } catch (PortalException e) {
            System.err.println("NO TITLE GIVEN FOR THIS QUESTION");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getJspPath(HttpServletRequest request, String template) {
        if (template.equals(TEMPLATE_ABSTRACT) || template.equals(TEMPLATE_FULL_CONTENT)) {

            return "/message_boards/asset/" + template + ".jsp";
        }
        else {
            return null;
        }
    }

    @Override
    public boolean include(HttpServletRequest request, HttpServletResponse response,
            String template) throws Exception {

        request.setAttribute(WebKeys.MESSAGE_BOARDS_MESSAGE, question);

        return super.include(request, response, template);
    }
}