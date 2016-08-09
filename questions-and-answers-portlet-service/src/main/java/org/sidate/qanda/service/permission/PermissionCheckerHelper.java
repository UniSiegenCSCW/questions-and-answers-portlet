package org.sidate.qanda.service.permission;

import com.liferay.portal.kernel.model.User;
import org.sidate.qanda.exception.NoSufficientAccessRightsException;

import static com.liferay.portal.kernel.security.permission.PermissionThreadLocal.getPermissionChecker;

/**
 * Created by Julian Dax on 04/08/16.
 */
public class PermissionCheckerHelper {
    private static final String defaultMessage = "Sie haben keinen Zugriff auf diese Ressource.";

    public static void checkIfUserIsSignedIn() throws NoSufficientAccessRightsException {
        if (!getPermissionChecker().isSignedIn()) {
            throw new NoSufficientAccessRightsException(defaultMessage);
        }
    }

    public static void checkPermission(Class entry, long id, String  action) throws NoSufficientAccessRightsException {
        User user = getPermissionChecker().getUser();
        Boolean hasPermission = getPermissionChecker().hasPermission(user.getGroupId(), entry.getName(), id, action);
        if(!hasPermission){
            throw new NoSufficientAccessRightsException(defaultMessage);
        }
    }

//    public static void checkIfUserOwns(long id) throws NoSufficientAccessRightsException {
//        getPermissionChecker().hasPermission()
//    }



}
