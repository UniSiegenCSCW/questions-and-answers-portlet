package de.sidate.question_and_answers.asset;

import com.liferay.asset.kernel.model.BaseAssetRenderer;
import de.sidate.questions_and_answers.model.Question;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Created by jk on 15.06.16.
 */

public class QuestionAssetRenderer extends BaseAssetRenderer {

    private Question question;

    public QuestionAssetRenderer(Question question) {
        this.question = question;
    }

    @Override
    public Object getAssetObject() {
        return null;
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
        return null;
    }

    @Override
    public String getTitle(Locale locale) {
        return question.getTitle();
    }

    @Override
    public boolean include(HttpServletRequest request, HttpServletResponse response, String template) throws Exception {
        return false;
    }
}
