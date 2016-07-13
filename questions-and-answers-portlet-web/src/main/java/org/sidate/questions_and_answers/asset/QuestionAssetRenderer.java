package org.sidate.questions_and_answers.asset;

import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.portal.kernel.exception.PortalException;
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

    private final Question QUESTION;

    public QuestionAssetRenderer(Question question) {
        QUESTION = question;
    }

    // EDIT AND VIEW PERMISSIONS MUST BE INSERTED HERE


    @Override
    public Object getAssetObject() {
        return QUESTION;
    }

    @Override
    public long getGroupId() {
        return QUESTION.getGroupId();
    }

    @Override
    public long getUserId() {
        return QUESTION.getUserId();
    }

    @Override
    public String getUserName() {
        return QUESTION.getUserName();
    }

    @Override
    public String getUuid() {
        return QUESTION.getUuid();
    }

    @Override
    public String getClassName() {
        return Question.class.getName();
    }

    @Override
    public long getClassPK() {
        return QUESTION.getQuestionID();
    }

    @Override
    public String getSummary(PortletRequest portletRequest, PortletResponse portletResponse) {
        return "THIS SHOULD BE A SUMMARY FOR QUESTION_ASSET_RENDERER";
    }

    @Override
    public String getTitle(Locale locale) {
        try {
            return QUESTION.getTitle();
        } catch (PortalException e) {
            System.err.println("NO TITLE GIVEN FOR THIS QUESTION");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getJspPath(HttpServletRequest request, String template) {
        if (template.equals(TEMPLATE_ABSTRACT) || template.equals(TEMPLATE_FULL_CONTENT)) {

            return "/questions/asset/" + template + ".jsp";
        }
        else {
            return null;
        }
    }

    @Override
    public boolean include(HttpServletRequest request, HttpServletResponse response,
            String template) throws Exception {

        request.setAttribute("question", QUESTION);

        return super.include(request, response, template);
    }
}