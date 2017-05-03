package org.sidate.qanda.portlet;

/**
 * Created by florian on 26.04.17.
 */

import aQute.bnd.annotation.metatype.Meta;

@Meta.OCD(id = "org.sidate.qanda.portlet.QuestionsAndAnswersPortletConfiguration")
public interface QuestionsAndAnswersPortletConfiguration {
    @Meta.AD(required = false)
    public String ratingsPortletUrl();

    @Meta.AD(required = false)
    public String ratingsPortletId();

    @Meta.AD(required = false)
    public String ratingsPortletLayoutId();
}
