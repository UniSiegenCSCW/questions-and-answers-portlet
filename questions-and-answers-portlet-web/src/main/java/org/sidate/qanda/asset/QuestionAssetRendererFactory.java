package org.sidate.qanda.asset;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;
import org.osgi.service.component.annotations.Component;
import org.sidate.qanda.model.Question;
import org.sidate.qanda.service.QuestionLocalServiceUtil;

/**
 * Created by jk on 08.07.16.
 */

@Component(
        immediate = true,
        property = {"javax.portlet.name=org_sidate_qanda_QuestionAndAnswersPortlet"},
        service = QuestionAssetRendererFactory.class
)

public class QuestionAssetRendererFactory extends BaseAssetRendererFactory<Question> {

    public QuestionAssetRendererFactory() {
        setSearchable(true);
        setLinkable(true);
    }

    @Override
    public AssetRenderer getAssetRenderer(long classPK, int type) throws PortalException {

        Question question = QuestionLocalServiceUtil.getQuestion(classPK);

        return new QuestionAssetRenderer(question);
    }

    @Override
    public String getType() {
        return "question";
    }
}
