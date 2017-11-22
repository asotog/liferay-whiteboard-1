<%--
/**
 * Copyright (C) 2005-2014 Rivet Logic Corporation.
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

<%-- Note: don't remove the css styles declared inline for some components in this page  --%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>

<liferay-theme:defineObjects />
<portlet:defineObjects />
<input type="hidden" class="profile-image-path" value="<%= HtmlUtil.escape(user.getPortraitURL(themeDisplay)) %>"/>

<div class="editor">
    <div class="users-online">
        <a href="#" class="expand-collapse-btn"><i class="icon-user"></i> <span class="count"></span><liferay-ui:message key="rivetlogic.whiteboard.users.online"/></a>
        <div class="users-online-wrapper">
            <header><h6><liferay-ui:message key="rivetlogic.whiteboard.users.header"/></h6></header>
            <div class="bd"></div>
        </div>
    </div>
    <div class="user-modification-tooltips">
    </div>
    <menu>
        <div class="btn-group btn-group-vertical">
            <button class="btn add" data-shape="rectangle" title='<liferay-ui:message key="rivetlogic.whiteboard.shapes.rectangle.title"/>'><span></span></button>
            <button class="btn add" data-shape="line" title='<liferay-ui:message key="rivetlogic.whiteboard.shapes.line.title"/>'><span></span></button>
            <button class="btn add" data-shape="circle" title='<liferay-ui:message key="rivetlogic.whiteboard.shapes.circle.title"/>'><span></span></button>
            <button class="btn free" title='<liferay-ui:message key="rivetlogic.whiteboard.shapes.free.title"/>'><i class="icon-pencil" ></i></button>
            <button class="btn add" data-shape="text" title='<liferay-ui:message key="rivetlogic.whiteboard.shapes.text.title"/>'><span></span></button>
            <button class="btn delete" title='<liferay-ui:message key="rivetlogic.whiteboard.shapes.remove.title"/>'><i class="icon-remove"></i></button>
            <button class="btn objects-options" title='<liferay-ui:message key="rivetlogic.whiteboard.shapes.objectsoptions.title"/>'>
                <i class="icon-cog"></i>
                <ul class="dropdown-menu text-left" role="menu" aria-labelledby="dropdownMenu">
                  <li><a data-action="send-to-back" href="javascript:void(0)"><liferay-ui:message key="rivetlogic.whiteboard.shapes.sendtoback.title"/></a></li>
                  <li><a data-action="bring-to-front" href="javascript:void(0)"><liferay-ui:message key="rivetlogic.whiteboard.shapes.bringtofront.title"/></a></li>
                </ul>
            </button>
            <button class="btn download" title='<liferay-ui:message key="rivetlogic.whiteboard.download.downloadaction"/>'><i class="icon-download-alt"></i></button>
            <button class="btn clean" title='<liferay-ui:message key="rivetlogic.whiteboard.clean.canvas.title"/>'><i class="icon-trash"></i></button>
            <div class="color-picker stroke yui3-skin-sam"><label><liferay-ui:message key="rivetlogic.whiteboard.stroke"/></label>
                <span class="sample" style="background-color: #000000;"></span>
                <div class="color-picker-container hidden">
                    <a href="#" class="close-picker"><liferay-ui:message key="rivetlogic.whiteboard.close"/></a>
                    <div class="picker">
                        <div id="hue-dial" class="hue-dial"></div>
                        <div class="sliders">
                            <div id="sat-slider" class="sat-slider"><strong><liferay-ui:message key="rivetlogic.whiteboard.saturation"/> <span></span></strong></div>
                            <div id="lum-slider" class="lum-slider"><strong><liferay-ui:message key="rivetlogic.whiteboard.luminance"/> <span></span></strong></div>
                        </div>
                        <div class="color" style="background-color: #000000;"></div>
                    </div>
                </div>
            </div>
            <div class="color-picker fill yui3-skin-sam"><label><liferay-ui:message key="rivetlogic.whiteboard.fill"/></label>
                <span class="sample" style="background-color: #FFFFFF;"></span>
                <div class="color-picker-container hidden">
                    <a href="#" class="close-picker"><liferay-ui:message key="rivetlogic.whiteboard.close"/></a>
                    <div class="picker">
                        <div id="hue-dial" class="hue-dial"></div>
                        <div class="sliders">
                            <div id="sat-slider" class="sat-slider"><strong><liferay-ui:message key="rivetlogic.whiteboard.saturation"/> <span></span></strong></div>
                            <div id="lum-slider" class="lum-slider"><strong><liferay-ui:message key="rivetlogic.whiteboard.luminance"/> <span></span></strong></div>
                        </div>
                        <div class="color" style="background-color: #FFFFFF;"></div>
                    </div>
                </div>
                <span class="opacity">
                    <label><liferay-ui:message key="rivetlogic.whiteboard.opacity"/></label>
                    <select>
                        <option value="1"><liferay-ui:message key="rivetlogic.whiteboard.opacity.1"/></option>
                        <option value="0.9"><liferay-ui:message key="rivetlogic.whiteboard.opacity.09"/></option>
                        <option value="0.8"><liferay-ui:message key="rivetlogic.whiteboard.opacity.08"/></option>
                        <option value="0.7"><liferay-ui:message key="rivetlogic.whiteboard.opacity.07"/></option>
                        <option value="0.6"><liferay-ui:message key="rivetlogic.whiteboard.opacity.06"/></option>
                        <option value="0.5"><liferay-ui:message key="rivetlogic.whiteboard.opacity.05"/></option>
                        <option value="0.4"><liferay-ui:message key="rivetlogic.whiteboard.opacity.04"/></option>
                        <option value="0.3"><liferay-ui:message key="rivetlogic.whiteboard.opacity.03"/></option>
                        <option value="0.2"><liferay-ui:message key="rivetlogic.whiteboard.opacity.02"/></option>
                        <option value="0.1"><liferay-ui:message key="rivetlogic.whiteboard.opacity.01"/></option>
                        <option value="0.000001"><liferay-ui:message key="rivetlogic.whiteboard.opacity.0"/></option>
                    </select>
                </span>
            </div>
        </div>
    </menu>
    <canvas id="editor-canvas" height="600"></canvas>
    <div class="text-editor hidden">
        <textarea class="text"></textarea>
        <button class="btn btn-primary edit"><liferay-ui:message key="rivetlogic.whiteboard.canvas.edit"/></button>
        <button class="btn cancel"><liferay-ui:message key="rivetlogic.whiteboard.canvas.cancel"/></button>
    </div>
</div>

<script id="users-online-template" type="text/x-handlebars-template">
    <ul class="unstyled">
        {{#each users}}
        <li><img src="{{userImagePath}}"/><span>{{userName}}</span></li>
        {{/each}}
    </ul>
</script>

<script id="user-tooltips-template" type="text/x-handlebars-template">
    <div id="{{id}}" style="top: {{top}}px; left: {{left}}px"><span class="sub-wrapper"><img src="{{userImagePath}}"/><span>{{userName}}</span></span></div>
</script>
