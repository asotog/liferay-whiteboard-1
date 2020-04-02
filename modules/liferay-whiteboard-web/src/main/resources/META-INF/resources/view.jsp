<%@ include file="/init.jsp" %>

<!-- <pre> -->
<!-- Debug: -->
<%-- Use custom websocket settings: ${prefBean.useCustomWebsocketUrl} --%>
<%-- Custom websocket base path: ${prefBean.customWebsocketBasePath} --%>
<!-- </pre> -->

<%
 PreferencesBean preferencesBean = (PreferencesBean) request.getAttribute("prefBean");
 String baseWebsocketURL = request.getServerName() + ":" + request.getServerPort();
 if (preferencesBean.getUseCustomWebsocketUrl()) {
	 baseWebsocketURL = baseWebsocketURL + preferencesBean.getCustomWebsocketBasePath();
 }
 String webSocketProtocol = "ws://";
 if (preferencesBean.getUseWebsocketSecured()) {
	 webSocketProtocol = "wss://";
 }
 String userImagePath = user.getPortraitURL(themeDisplay);
 String websocketURL = webSocketProtocol + baseWebsocketURL + CollaborationEndpoint.PATH;
 websocketURL = HttpUtil.addParameter(websocketURL, "userId", user.getUserId());
 websocketURL = HttpUtil.addParameter(websocketURL, "userImagePath", userImagePath);
 websocketURL = HttpUtil.addParameter(websocketURL, "guestLabel",  LanguageUtil.get(request, "rivetlogic.whiteboard.guest.name.label"));
%>
<script>
    window.CollaborationWhiteboardPortlet = {
        strings: {
            'rivetlogic.whiteboard.confirm.deleteallpopup.title': '<liferay-ui:message key="rivetlogic.whiteboard.confirm.deleteallpopup.title"/>',
            'rivetlogic.whiteboard.confirm.deleteallpopup.message': '<liferay-ui:message key="rivetlogic.whiteboard.confirm.deleteallpopup.message"/>',
            'rivetlogic.whiteboard.confirm.deleteshapepopup.title': '<liferay-ui:message key="rivetlogic.whiteboard.confirm.deleteshapepopup.title"/>',
            'rivetlogic.whiteboard.confirm.deleteshapepopup.message': '<liferay-ui:message key="rivetlogic.whiteboard.confirm.deleteshapepopup.message"/>',
            'rivetlogic.whiteboard.confirm.deleteagrouppopup.title': '<liferay-ui:message key="rivetlogic.whiteboard.confirm.deleteagrouppopup.title"/>',
            'rivetlogic.whiteboard.confirm.deleteagrouppopup.message': '<liferay-ui:message key="rivetlogic.whiteboard.confirm.deleteagrouppopup.message"/>',
            'rivetlogic.whiteboard.edit.text': '<liferay-ui:message key="rivetlogic.whiteboard.edit.text"/>',
            'rivetlogic.whiteboard.confirm.label': '<liferay-ui:message key="rivetlogic.whiteboard.confirm.label"/>',
            'rivetlogic.whiteboard.cancel.label': '<liferay-ui:message key="rivetlogic.whiteboard.cancel.label"/>',
            'rivetlogic.whiteboard.guest.name.label': '<liferay-ui:message key="rivetlogic.whiteboard.guest.name.label"/>',
            'rivetlogic.whiteboard.download.downloadaction': '<liferay-ui:message key="rivetlogic.whiteboard.download.downloadaction"/>'
        }
    };
</script>
<aui:script use="collaboration-whiteboard-portlet">
    A.CollaborationWhiteboardPortlet.init("<%= websocketURL %>", "<%=themeDisplay.getUser().getFullName() %>");
</aui:script>
<input type="hidden" class="profile-image-path" value="<%=userImagePath %>" />
<div class="connection-lost-alert alert alert-danger d-none" role="alert">Connection was lost please refresh the page. <a href="javascript:document.location.reload()">Reload</a></div>
<div class="communication-unsupported-alert alert alert-danger d-none" role="alert">Communication protocol not supported by your current browser. Please try <a href="http://caniuse.com/#search=websocket"
        target="_blank">supported</a> browser</div>
<div class="editor">
    <div class="users-online">
        <a href="#" class="expand-collapse-btn"><i class="icon-user"></i> <span class="count"></span> <liferay-ui:message key="rivetlogic.whiteboard.users.online"/></a>
        <div class="users-online-wrapper">
            <header>
                <h6>
                    <liferay-ui:message key="rivetlogic.whiteboard.users.header" />
                </h6>
            </header>
            <div class="bd"></div>
        </div>
    </div>
    <div class="user-modification-tooltips">
    </div>
    <menu>
        <div class="btn-group-vertical">
            <button class="btn add" data-shape="rectangle" title='<liferay-ui:message key="rivetlogic.whiteboard.shapes.rectangle.title"/>'><clay:icon symbol="square" /></button>
            <button class="btn add" data-shape="line" title='<liferay-ui:message key="rivetlogic.whiteboard.shapes.line.title"/>'><clay:icon symbol="hr" /></button>
            <button class="btn add" data-shape="circle" title='<liferay-ui:message key="rivetlogic.whiteboard.shapes.circle.title"/>'><clay:icon symbol="circle" /></button>
            <button class="btn free" title='<liferay-ui:message key="rivetlogic.whiteboard.shapes.free.title"/>'><clay:icon symbol="pencil" /></button>
            <button class="btn add" data-shape="text" title='<liferay-ui:message key="rivetlogic.whiteboard.shapes.text.title"/>'><clay:icon symbol="text" /></button>
            <button class="btn delete" title='<liferay-ui:message key="rivetlogic.whiteboard.shapes.remove.title"/>'><i class="icon-remove"></i></button>
            <div class="dropdown-menu-wrapper">
                <button class="btn objects-options" title='<liferay-ui:message key="rivetlogic.whiteboard.shapes.objectsoptions.title"/>'>
                    <clay:icon symbol="cog" />
                </button>
                <ul class="dropdown-menu text-left" role="menu" aria-labelledby="dropdownMenu">
                    <li><a data-action="send-to-back" href="javascript:void(0)"><liferay-ui:message key="rivetlogic.whiteboard.shapes.sendtoback.title"/></a></li>
                    <li><a data-action="bring-to-front" href="javascript:void(0)"><liferay-ui:message key="rivetlogic.whiteboard.shapes.bringtofront.title"/></a></li>
                </ul>
            </div>
            <button class="btn download" title='<liferay-ui:message key="rivetlogic.whiteboard.download.downloadaction"/>'><clay:icon symbol="download" /></button>
            <button class="btn clean" title='<liferay-ui:message key="rivetlogic.whiteboard.clean.canvas.title"/>'><clay:icon symbol="trash" /></button>
            <div class="color-picker stroke yui3-skin-sam"><label><liferay-ui:message key="rivetlogic.whiteboard.stroke"/></label>
                <span class="sample" style="background-color: #000000;"></span>
                <div class="color-picker-container d-none">
                    <a href="#" class="close-picker">
                        <liferay-ui:message key="rivetlogic.whiteboard.close" />
                    </a>
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
                <div class="color-picker-container d-none">
                    <a href="#" class="close-picker">
                        <liferay-ui:message key="rivetlogic.whiteboard.close" />
                    </a>
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
    <div class="text-editor d-none">
        <textarea class="text"></textarea>
        <button class="btn btn-primary edit"><liferay-ui:message key="rivetlogic.whiteboard.canvas.edit"/></button>
        <button class="btn cancel"><liferay-ui:message key="rivetlogic.whiteboard.canvas.cancel"/></button>
    </div>
</div>
<script id="users-online-template" type="text/x-handlebars-template">
    <ul class="list-unstyled">
        {{#each users}}
        <li><img src="{{userImagePath}}" /><span>{{userName}}</span></li>
        {{/each}}
    </ul>
</script>

<script id="user-tooltips-template" type="text/x-handlebars-template">
    <div id="{{id}}" style="top: {{top}}px; left: {{left}}px"><span class="sub-wrapper"><img src="{{userImagePath}}"/><span>{{userName}}</span></span>
    </div>
</script>