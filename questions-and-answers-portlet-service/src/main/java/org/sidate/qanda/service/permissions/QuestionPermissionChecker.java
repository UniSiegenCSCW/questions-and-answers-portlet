package org.sidate.qanda.service.permissions;

import com.liferay.exportimport.kernel.staging.permission.StagingPermissionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.BaseModelPermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import org.osgi.service.component.annotations.Component;
import org.sidate.qanda.model.Question;
import org.sidate.qanda.service.QuestionLocalServiceUtil;


@Component(
        immediate = true,
        property = {"model.class.name=org.sidate.quanda.model.Question"}
)
public class QuestionPermissionChecker implements BaseModelPermissionChecker {

    private final static String QUESTION_CLASS_NAME = Question.class.getName();

    @Override
    public void checkBaseModel(PermissionChecker permissionChecker, long groupId, long primaryKey, String actionId) throws PortalException {
        check(permissionChecker, QuestionLocalServiceUtil.getQuestion(primaryKey), actionId);
    }

    public static boolean contains(PermissionChecker permissionChecker, Question question, String actionId) {

        final long QUESTION_ID = question.getQuestionID();
        final long GROUP_ID = question.getGroupId();

        Boolean hasPermission = StagingPermissionUtil.hasPermission(permissionChecker, GROUP_ID, QUESTION_CLASS_NAME,
                QUESTION_ID, question.getPortletId(), actionId);

        if (hasPermission != null) {
            return hasPermission;
        }

        return permissionChecker.hasOwnerPermission(question.getCompanyId(), QUESTION_CLASS_NAME, QUESTION_ID,
                question.getUserId(), actionId)
                || permissionChecker.hasPermission(GROUP_ID, QUESTION_CLASS_NAME, QUESTION_ID, actionId);
    }

    public static void check(PermissionChecker permissionChecker, Question question, String actionId)
            throws PortalException {

        if (!contains(permissionChecker, question, actionId)) {
            throw new PrincipalException.MustHavePermission(permissionChecker, QUESTION_CLASS_NAME ,
                    question.getQuestionID(), actionId);
        }
    }
}
