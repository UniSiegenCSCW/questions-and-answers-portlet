package org.sidate.qanda.portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import aQute.bnd.annotation.metatype.Configurable;
import org.osgi.service.component.annotations.Modified;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by florian on 03.05.17.
 */

@Component(
        configurationPid = "org.sidate.qanda.portlet.QuestionsAndAnswersPortletConfiguration",
        configurationPolicy = ConfigurationPolicy.OPTIONAL,
        immediate = true,
        property = {
                "javax.portlet.name=org_sidate_qanda_portlet_QuestionsAndAnswersPortlet",
                "javax.portlet.security-role-ref=admin"
        },
        service = ConfigurationAction.class
)

public class QuestionAndAnswersConfigurationAction extends DefaultConfigurationAction {

    private static final Log log = LogFactoryUtil.getLog(QuestionAndAnswersConfigurationAction.class);
    private volatile QuestionsAndAnswersPortletConfiguration questionsAndAnswersPortletConfiguration;

    @Override
    public void processAction(PortletConfig portletConfig, ActionRequest actionRequest, ActionResponse actionResponse)
            throws Exception {
        String selectedPagePortlet = ParamUtil.getString(actionRequest, "PARAM_CONFIG_RATINGS_PORTLET");
        String[] params = selectedPagePortlet.split(" ");
        if (params.length>=3) {
            String ratingsPortletLayouId = params[0];
            log.info("processAction. LayouId: " + ratingsPortletLayouId);
            setPreference(actionRequest, "ratingsPortletLayoutId", ratingsPortletLayouId);
            String ratingsPortletId = params[1];
            log.info("processAction. PortletId: " + ratingsPortletId);
            setPreference(actionRequest, "ratingsPortletId", ratingsPortletId);
            String ratingsPortletUrl = params[2];
            log.info("processAction. Url: " + ratingsPortletUrl);
            setPreference(actionRequest, "ratingsPortletUrl", ratingsPortletUrl);

        }
        super.processAction(portletConfig, actionRequest, actionResponse);
    }

    @Override
    public void include(PortletConfig portletConfig, HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse) throws Exception {
        httpServletRequest.setAttribute(QuestionsAndAnswersPortletConfiguration.class.getName(), questionsAndAnswersPortletConfiguration);
        super.include(portletConfig, httpServletRequest, httpServletResponse);
    }

    @Activate
    @Modified
    protected void activate(Map<Object, Object> properties) {
        questionsAndAnswersPortletConfiguration = Configurable.createConfigurable(QuestionsAndAnswersPortletConfiguration.class, properties);
    }
}
