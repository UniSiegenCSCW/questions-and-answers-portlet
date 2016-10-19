package org.sidate.qanda.service.permissions;


import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import org.sidate.qanda.model.Question;

public class QuestionPermission {

    private static final String RESOURCE_NAME = "org.sidate.quanda.model.Question";

    public static void check(PermissionChecker permissionChecker, Question question, String actionId)
            throws PrincipalException {

        if (!contains(permissionChecker, question, actionId)) {
            throw new PrincipalException();
        }
    }

    public static boolean contains(PermissionChecker permissionChecker, Question question, String actionId) {

        return permissionChecker.hasPermission(question.getGroupId(), RESOURCE_NAME, question.getQuestionID(), actionId);
    }
}
