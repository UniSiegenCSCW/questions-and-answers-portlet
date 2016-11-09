<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<liferay-ui:error key="EmptyQuestionTitleException" message="Der Titel einer Frage darf nicht leer sein" />
<liferay-ui:error key="EmptyQuestionTextException" message="Der Text einer Frage darf nicht leer sein" />

<portlet:defineObjects />

<liferay-theme:defineObjects />