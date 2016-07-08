package org.sidate.questions_and_answers.asset;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;
import org.osgi.service.component.annotations.Component;
import org.sidate.questions_and_answers.model.Question;
import org.sidate.questions_and_answers.service.QuestionLocalServiceUtil;

/**
 * Created by jk on 08.07.16.
 */

@Component(
        immediate = true,
        property = {"javax.portlet.name=org.sidate.questions_and_answers"},
        service = AssetRendererFactory.class
)

public class QuestionAssetRendererFactory extends BaseAssetRendererFactory {

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
