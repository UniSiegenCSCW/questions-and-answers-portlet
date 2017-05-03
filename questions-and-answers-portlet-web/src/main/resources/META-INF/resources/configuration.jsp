<%@ page import="org.sidate.qanda.portlet.QuestionsAndAnswersPortletConfiguration" %>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ page import="com.liferay.portal.kernel.model.Layout" %>
<%@ page import="java.util.List" %>
<%@ page import="com.liferay.portal.kernel.service.LayoutLocalServiceUtil" %>
<%@ page import="com.liferay.portal.kernel.util.*" %>
<%@ page import="com.liferay.portal.kernel.theme.ThemeDisplay" %>
<%@ page import="com.liferay.portal.kernel.model.LayoutTypePortlet" %>
<%@ page import="com.liferay.portal.kernel.model.Portlet" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<portlet:defineObjects />

<liferay-theme:defineObjects />

<%@ include file="initConfiguration.jsp" %>

<%
    HttpServletRequest httpRequest=null;
    String saveLabel= LanguageUtil.get(httpRequest,"save");
    Long groupId = themeDisplay.getSiteGroupId();//getLayout().getGroupId();
    String selectedValue= ratingsPortletLayoutId + " " + ratingsPortletId + " " + ratingsPortletUrl;

    List<Layout> pagesLayouts = LayoutLocalServiceUtil.getLayouts(groupId, true);

    String currentValue = ratingsPortletLayoutId + " " + ratingsPortletId + " " + ratingsPortletUrl;

%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />
<div class="container-fluid-1280 main-content-card">
    <div class="panel-heading">
        <div class="panel-title">
            <liferay-ui:message key="preferences"/>
        </div>
    </div>
    <fieldset class="panel panel-default">

        <aui:form action="<%= configurationActionURL %>" method="post" name="fm">

            <aui:input name="<%= Constants.CMD %>" type="hidden"
                       value="<%= Constants.UPDATE %>" />

            <aui:input name="redirect" type="hidden"
                       value="<%= configurationRenderURL %>" />

            <aui:fieldset>

                <aui:select name="PARAM_CONFIG_RATINGS_PORTLET" label="Link zum Ratings Portlet" value="<%= selectedValue%>">
                    <%
                    for (Layout pageLayout : pagesLayouts) {

                        String url=com.liferay.portal.kernel.util.PortalUtil.getLayoutFriendlyURL(pageLayout, themeDisplay);
                        System.out.println(pageLayout.getName()+" "+" Friendly: "+pageLayout.getFriendlyURL());
                        System.out.println("URL:"+url);
                        String friendlyUrl=pageLayout.getFriendlyURL();
                        LayoutTypePortlet layoutTypePortletInPage = (LayoutTypePortlet)pageLayout.getLayoutType();
                        List <Portlet> actualPortlets = layoutTypePortletInPage.getAllPortlets();
                        long pageLayoutId=pageLayout.getPlid();
                            for (Portlet portlet: actualPortlets) {
                                String portletId=portlet.getPortletId();
                                String optionValue=pageLayoutId + " " + portletId + " "  + url;
                            %>
                            <aui:option value="<%=optionValue%>"><%=friendlyUrl+" \""+portlet.getDisplayName()+"\""%></aui:option>
                            <%
                        }
                    }
                    %>
                </aui:select>

            </aui:fieldset>
            <aui:button-row>
                <aui:button type="submit" value="save"></aui:button>
            </aui:button-row>
        </aui:form>

    </fieldset>

    <%--<div class="panel-heading">
        <div class="panel-title">
            <liferay-ui:message key="version"/>
        </div>
    </div>
    <fieldset class="panel panel-default font-smaller ratings-version-content">

        <%
            String [] versions=SidateRatingsPortlet.getBundleInfo();
            for (String version:versions){ %>
                <label><%=version%></label>
              <!--  <div class="ratings-version"></div>-->
        <%
            }
        %>

    </fieldset>--%>

</div>

