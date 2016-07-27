package org.sidate.questions_and_answers.service.render.question;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.osgi.service.component.annotations.Component;
import org.sidate.questions_and_answers.model.Question;
import org.sidate.questions_and_answers.service.QuestionLocalServiceUtil;

/**
 * Created by adhominem on 27.07.16.
 */

@Component(immediate = true,
        service = AssetRendererFactory.class,
        property = {"javax.portlet.resource-bundle=content.Language"})
public class QuestionAssetRendererFactory extends BaseAssetRendererFactory<Question> {

    private static final Log log = LogFactoryUtil.getLog(QuestionAssetRendererFactory.class);

    public QuestionAssetRendererFactory() {
        setClassName(Question.class.getName());
        setLinkable(true);
        setSearchable(true);
        setSupportsClassTypes(true);
    }

    @Override
    public AssetRenderer<Question> getAssetRenderer(long classPK, int type) {
        Question question = QuestionLocalServiceUtil.fetchQuestion(classPK);

        QuestionAssetRenderer questionAssetRenderer = new QuestionAssetRenderer(question);
        questionAssetRenderer.setAssetRendererType(type);
        return questionAssetRenderer;
    }

    @Override
    public String getType() {
        return "content_question";
    }
}
