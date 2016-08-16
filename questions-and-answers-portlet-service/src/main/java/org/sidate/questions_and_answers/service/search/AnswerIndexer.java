package org.sidate.questions_and_answers.service.search;

import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.*;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import org.osgi.service.component.annotations.Component;
import org.sidate.questions_and_answers.model.Answer;
import org.sidate.questions_and_answers.service.AnswerLocalServiceUtil;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import java.util.Date;
import java.util.Locale;

/**
 * Created by User on 05.08.2016.
 */

@Component(immediate = true, service = Indexer.class)
public class AnswerIndexer  extends BaseIndexer<Answer> {

    private static final Log log = LogFactoryUtil.getLog(QuestionIndexer.class);

    private static final String CLASS_NAME = Answer.class.getName();

    public AnswerIndexer(){
        setDefaultSelectedFieldNames(
                Field.USER_NAME, Field.CONTENT);
        setFilterSearch(true);
        //setPermissionAware(true);
        setSelectAllLocales(true);
    }

    @Override
    protected void doDelete(Answer answer) throws Exception {
        deleteDocument(answer.getCompanyId(), answer.getAnswerID());
    }

    @Override
    protected Document doGetDocument(Answer answer) throws Exception {
        Document document = getBaseModelDocument(CLASS_NAME, answer);

        //document.addText(Field.CAPTION, object.getCoverImageCaption());
        document.addText(Field.CONTENT, HtmlUtil.extractText(answer.getText()));
        document.addText(Field.USER_NAME, HtmlUtil.extractText(answer.getUserName()));
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
        Answer answer = AnswerLocalServiceUtil.fetchAnswer(classPK);
        doReindex(answer);
    }

    private void reindexEntries(long companyId) throws PortalException {
        final IndexableActionableDynamicQuery indexableActionableDynamicQuery =
                AnswerLocalServiceUtil.getIndexableActionableDynamicQuery();

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
                answer -> {
                    try {
                        Document document = getDocument((Answer) answer);
                        indexableActionableDynamicQuery.addDocuments(document);
                    } catch (PortalException pe) {
                        if (log.isWarnEnabled()) {
                            log.warn("Unable to index Ratings3DEntry " + ((Answer) answer).getAnswerID(), pe);
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
    protected void doReindex(Answer answer) throws Exception {
        Document document = getDocument(answer);
        IndexWriterHelperUtil.updateDocument(
                getSearchEngineId(), answer.getCompanyId(), document,
                isCommitImmediately());
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }
}
