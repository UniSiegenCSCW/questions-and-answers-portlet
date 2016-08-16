package org.sidate.questions_and_answers.service.render.answer;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.osgi.service.component.annotations.Component;
import org.sidate.questions_and_answers.model.Answer;
import org.sidate.questions_and_answers.service.AnswerLocalServiceUtil;

/**
 * Created by User on 05.08.2016.
 */

@Component(immediate = true,
        service = AssetRendererFactory.class,
        property = {"javax.portlet.resource-bundle=content.Language"})
public class AnswerAssetRendererFactory extends BaseAssetRendererFactory<Answer> {

    private static final Log log = LogFactoryUtil.getLog(AnswerAssetRendererFactory.class);

    public AnswerAssetRendererFactory(){
        setClassName(Answer.class.getName());
        setLinkable(true);
        setSearchable(true);
        setSupportsClassTypes(true);
    }

    @Override
    public AssetRenderer<Answer> getAssetRenderer(long classPK, int type) throws PortalException {
        Answer answer = AnswerLocalServiceUtil.fetchAnswer(classPK);

        AnswerAssetRenderer answerAssetRenderer = new AnswerAssetRenderer(answer);
        answerAssetRenderer.setAssetRendererType(type);
        return answerAssetRenderer;
    }

    @Override
    public String getType() {
        return "content_answer";
    }
}
