<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="org.sidate.qanda.portlet.QuestionsAndAnswersPortletConfiguration" %>
<%@ page import="com.liferay.portal.kernel.util.StringPool" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>


<portlet:defineObjects />

<liferay-theme:defineObjects />

<%

    QuestionsAndAnswersPortletConfiguration questionsAndAnswersPortletConfiguration =
            (QuestionsAndAnswersPortletConfiguration)renderRequest.getAttribute(QuestionsAndAnswersPortletConfiguration.class.getName());
    String ratingsPortletUrl = StringPool.BLANK;
    String ratingsPortletId = StringPool.BLANK;
    long ratingsPortletLayoutId =-1;
    boolean noConfig=true;

    noConfig = Validator.isNull(questionsAndAnswersPortletConfiguration);

    if (!noConfig){
        String ratingsPortletLayoutIdS = portletPreferences.getValue("ratingsPortletLayoutId", questionsAndAnswersPortletConfiguration.ratingsPortletLayoutId());
        try {
            ratingsPortletLayoutId = Long.parseLong(ratingsPortletLayoutIdS);
        } catch (Exception er){
            System.out.println("Error during Setup/Parsing of link to Portlet ratings portlet in q and a portlet! LayoutId in preferences:"+ratingsPortletLayoutIdS);
        }
        ratingsPortletId = portletPreferences.getValue("ratingsPortletId", questionsAndAnswersPortletConfiguration.ratingsPortletId());
        ratingsPortletUrl = portletPreferences.getValue("ratingsPortletUrl", questionsAndAnswersPortletConfiguration.ratingsPortletUrl());
    }

%>

