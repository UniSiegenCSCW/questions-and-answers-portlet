package org.sidate.qanda.service.permissions;

import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import org.sidate.qanda.model.Question;

public class QandAPermission {

    private static final String RESOURCE_NAME = "org.sidate.quanda.model";

    public static void check(PermissionChecker permissionChecker, long groupId, String actionId)
            throws PrincipalException {

        if (!contains(permissionChecker, groupId, actionId)) {
            throw new PrincipalException();
        }
    }

    public static boolean contains(PermissionChecker permissionChecker, long groupId, String actionId) {

        return permissionChecker.hasPermission(groupId, RESOURCE_NAME, groupId, actionId);
    }
}
