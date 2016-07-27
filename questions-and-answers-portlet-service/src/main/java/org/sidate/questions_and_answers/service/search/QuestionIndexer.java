package org.sidate.questions_and_answers.service.search;

import com.liferay.portal.kernel.dao.orm.*;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.*;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import org.sidate.questions_and_answers.model.Question;
import org.sidate.questions_and_answers.service.QuestionLocalServiceUtil;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import java.util.Date;
import java.util.Locale;

/**
 * Created by adhominem on 27.07.16.
 */

public class QuestionIndexer extends BaseIndexer<Question> {

    private static final Log log = LogFactoryUtil.getLog(QuestionIndexer.class);

    private static final String CLASS_NAME = Question.class.getName();

    @Override
    protected void doDelete(Question question) throws Exception {
        deleteDocument(question.getCompanyId(), question.getQuestionID());
    }

    @Override
    protected Document doGetDocument(Question question) throws Exception {
        Document document = getBaseModelDocument(CLASS_NAME, question);

        //document.addText(Field.CAPTION, object.getCoverImageCaption());
        document.addText(Field.CONTENT, HtmlUtil.extractText(question.getText()));
        String COMMENT = "Frage";
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

    private void reindexEntries(long companyId) throws PortalException {
        final IndexableActionableDynamicQuery indexableActionableDynamicQuery =
                QuestionLocalServiceUtil.getIndexableActionableDynamicQuery();

        indexableActionableDynamicQuery.setAddCriteriaMethod(
                dynamicQuery -> {
                    Property displayDateProperty = PropertyFactoryUtil.forName("displayDate");
                    dynamicQuery.add(displayDateProperty.lt(new Date()));
                    Property statusProperty = PropertyFactoryUtil.forName("status");
                    Integer[] statuses = {
                            WorkflowConstants.STATUS_APPROVED,
                            WorkflowConstants.STATUS_IN_TRASH
                    };
                    dynamicQuery.add(statusProperty.in(statuses));
                });
        indexableActionableDynamicQuery.setCompanyId(companyId);
        indexableActionableDynamicQuery.setPerformActionMethod(
                question -> {
                    try {
                        Document document = getDocument((Question) question);
                        indexableActionableDynamicQuery.addDocuments(document);
                    } catch (PortalException pe) {
                        if (log.isWarnEnabled()) {
                            log.warn("Unable to index Ratings3DEntry " + ((Question) question).getQuestionID(), pe);
                        }
                    }
                });
        indexableActionableDynamicQuery.setSearchEngineId(getSearchEngineId());
        indexableActionableDynamicQuery.performActions();
    }

    @Override
    protected void doReindex(String[] ids) throws Exception {
        long companyId = GetterUtil.getLong(ids[0]);
        reindexEntries(companyId);
    }

    @Override
    protected void doReindex(Question question) throws Exception {
        Document document = getDocument(question);
        IndexWriterHelperUtil.updateDocument(
                getSearchEngineId(), question.getCompanyId(), document,
                isCommitImmediately());
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }
}
