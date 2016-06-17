package de.sidate.question_and_answers.asset;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;
import de.sidate.questions_and_answers.model.Question;
import de.sidate.questions_and_answers.service.QuestionLocalServiceUtil;

/**
 * Created by jk on 15.06.16.
 */

public class QuestionAssetRendererFactory extends BaseAssetRendererFactory {

    private static final String TYPE = "question";

    @Override
    public AssetRenderer getAssetRenderer(long classPK, int type) throws PortalException {
            Question question = QuestionLocalServiceUtil.getQuestion(classPK);
            return new QuestionAssetRenderer(question);
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
