package org.sidate.questions_and_answers.service.search;

import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.util.HtmlUtil;
import org.sidate.questions_and_answers.model.Question;
import org.sidate.questions_and_answers.service.QuestionLocalServiceUtil;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import java.util.Locale;

/**
 * Created by adhominem on 27.07.16.
 */

public class QuestionIndexer extends BaseIndexer<Question> {

    private static final String CLASS_NAME = Question.class.getName();
    private final String COMMENT = "Frage";

    @Override
    protected void doDelete(Question question) throws Exception {
        deleteDocument(question.getCompanyId(), question.getQuestionID());
    }

    @Override
    protected Document doGetDocument(Question question) throws Exception {
        Document document = getBaseModelDocument(CLASS_NAME, question);

        //document.addText(Field.CAPTION, object.getCoverImageCaption());
        document.addText(Field.CONTENT, HtmlUtil.extractText(question.getText()));
        document.addText(Field.DESCRIPTION, COMMENT);
        document.addDate(Field.MODIFIED_DATE, question.getModifiedDate());
        document.addDate(Field.CREATE_DATE, question.getCreateDate());
        //document.addText(Field.SUBTITLE, HtmlUtil.extractText(object.getComment()));
        //String title= RatingsUtil.getBriefTitleFromContent(HtmlUtil.extractText(question.getTitle()));
        document.addText(Field.TITLE, question.getTitle());
        document.addText(Field.USER_NAME, question.getUserName());
        document.addText(Field.TYPE, COMMENT);

        return document;
    }

    @Override
    protected Summary doGetSummary(Document document, Locale locale, String snippet, PortletRequest portletRequest, PortletResponse portletResponse) throws Exception {
        Summary summary = createSummary(document);
        summary.setMaxContentLength(500);
        return summary;
    }

    @Override
    protected void doReindex(String className, long classPK) throws Exception {
        Question question = QuestionLocalServiceUtil.fetchQuestion(classPK);
        doReindex(question);
    }

    @Override
    protected void doReindex(String[] ids) throws Exception {

    }

    @Override
    protected void doReindex(Question object) throws Exception {

    }

    @Override
    public String getClassName() {
        return null;
    }
}
