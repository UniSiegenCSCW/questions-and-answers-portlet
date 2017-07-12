package org.sidate.qanda.service.social;

/**
 * Created by florian on 28.06.17.
 */
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.social.kernel.util.SocialConfigurationUtil;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


@Component(immediate = true, service = QandASocialActivityConfigurator.class, property = {"javax.portlet.resource-bundle=content.Language"})
public class QandASocialActivityConfigurator {

    @Activate
    protected void activate() throws Exception {
        Class<?> clazz = getClass();

        String xml = new String(
                FileUtil.getBytes(getClass(), "/META-INF/social/liferay-social.xml"));

        SocialConfigurationUtil.read(
                clazz.getClassLoader(), new String[]{xml});
    }

    @Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
    protected void setModuleServiceLifecycle(
            ModuleServiceLifecycle moduleServiceLifecycle) {
    }

}
