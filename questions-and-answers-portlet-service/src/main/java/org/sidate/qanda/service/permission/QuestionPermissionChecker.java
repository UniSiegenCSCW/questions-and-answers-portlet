package org.sidate.qanda.service.permission;

import static com.liferay.exportimport.kernel.staging.permission.StagingPermissionUtil.hasPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.BaseModelPermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.sidate.qanda.model.Question;
import org.sidate.qanda.service.QuestionLocalService;


/**
 * Created by Julian Dax on 04/08/16.
 */

@Component(
        immediate = true,
        property = {"model.class.name=org.sidate.qanda.model.Question"},
        service = BaseModelPermissionChecker.class
)
public class QuestionPermissionChecker implements BaseModelPermissionChecker {
    private static Log log = LogFactoryUtil.getLog(QuestionPermissionChecker.class);
    private static final String portletKey = "org_sidate_qanda_questions-and-answers-portlet";

    @Override
    public void checkBaseModel(PermissionChecker permissionChecker, long groupId, long primaryKey, String actionId) throws PortalException {
        Boolean hasPermission = hasPermission(permissionChecker, groupId, Question.class.getName(), primaryKey, portletKey, actionId);
        if(!hasPermission){
            throw new PrincipalException.MustHavePermission(
                    permissionChecker, Question.class.getName(),
                    primaryKey, actionId);
        }
    }


}
