<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<liferay-ui:error key="EmptyQuestionTitleException" message="Der Titel einer Frage darf nicht leer sein" />
<liferay-ui:error key="EmptyQuestionTextException" message="Der Text einer Frage darf nicht leer sein" />
<liferay-ui:error key="EmptyAnswerTextException" message="Der Text einer Antwort darf nicht leer sein" />
<liferay-ui:success key="questionAdded" message="Die Frage wurde erfolgreich hinzugef&uuml;gt" />
<liferay-ui:success key="answerAdded" message="Die Antwort wurde erfolgreich hinzugef&uuml;gt" />
<liferay-ui:success key="questionEdited" message="Die Frage wurde erfolgreich editiert" />
<liferay-ui:success key="answerEdited" message="Die Antwort wurde erfolgreich editiert" />

<portlet:defineObjects />

<liferay-theme:defineObjects />