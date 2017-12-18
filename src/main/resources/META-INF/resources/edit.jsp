<%--
/**
 * Copyright (C) 2005-2016 Rivet Logic Corporation.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; version 3 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 */
--%>

<%@page import="com.rivetlogic.whiteboard.portlet.Constants"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet" %>
<%@ page import="com.liferay.portal.kernel.util.WebKeys" %>
<%@ page import="com.liferay.portal.kernel.util.GetterUtil"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<liferay-theme:defineObjects />
<portlet:defineObjects />
<!-- <pre> -->
<!-- Debug: -->
<%-- Use custom websocket settings: ${prefBean.useCustomWebsocketUrl} --%>
<%-- Custom websocket base path: ${prefBean.customWebsocketBasePath} --%>
<!-- </pre> -->

<portlet:renderURL var="currentURL">
    <portlet:param name="mvcPath" value="/edit.jsp"/>
</portlet:renderURL>

<portlet:actionURL name="savePreferences" var="savePreferencesURL">
    <portlet:param name="redirect" value="${currentURL}"/>
</portlet:actionURL>

<aui:form name="fm" action="${savePreferencesURL}">
    <div class="panel panel-default">
        <div class="panel-heading"><liferay-ui:message key="label.websocket.settings"/></div>
        <div class="panel-body">
            <aui:input value="${prefBean.useCustomWebsocketUrl}" name="<%=Constants.USE_CUSTOM_WEBSOCKET_URL %>" type="checkbox" label="label.websocket.usecustomurl"></aui:input>
            <aui:input value="${prefBean.customWebsocketBasePath}" name="<%=Constants.CUSTOM_WEBSOCKET_BASE_PATH %>" type="text" label="label.websocket.custombasepath"></aui:input>
        </div>
    </div>
    
    <aui:button-row>
        <aui:button type="submit" value="submit"/>
    </aui:button-row>
</aui:form>