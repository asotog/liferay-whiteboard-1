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
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package com.rivetlogic.whiteboard.portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

public class PreferencesBean {
	private boolean useCustomWebsocketUrl;
	private boolean useWebsocketSecured;
	private String customWebsocketBasePath;
	private String portletId;
	
	private static final Log _log = LogFactoryUtil.getLog(PreferencesBean.class);
	
	public PreferencesBean(PortletRequest request, boolean useCustomWebsocketUrl, boolean useWebsocketSecured, String customWebsocketUrl){
		PortletPreferences preferences = request.getPreferences();
		
		portletId = GetterUtil.getString(request.getAttribute(WebKeys.PORTLET_ID));
		this.useCustomWebsocketUrl = GetterUtil.getBoolean(preferences.getValue(Constants.USE_CUSTOM_WEBSOCKET_URL,
				String.valueOf(useCustomWebsocketUrl)));
		this.useWebsocketSecured = GetterUtil.getBoolean(preferences.getValue(Constants.USE_WEBSOCKET_SECURED, 
				String.valueOf(useWebsocketSecured)));
		this.customWebsocketBasePath = GetterUtil.getString(preferences.getValue(Constants.CUSTOM_WEBSOCKET_BASE_PATH,
				String.valueOf(customWebsocketUrl)));
		_log.debug("useCustomWebsocketUrl:" + this.useCustomWebsocketUrl);
	}
	
	public PreferencesBean(){
		useCustomWebsocketUrl = Constants.DEFAULT_USE_CUSTOM_WEBSOCKET_BASE_PATH;
		customWebsocketBasePath = Constants.DEFAULT_CUSTOM_WEBSOCKET_BASE_PATH;
		useWebsocketSecured = Constants.DEFAULT_USE_WEBSOCKET_SECURED;
	}
	
	public void save(PortletRequest request) throws ReadOnlyException, ValidatorException, IOException{
		PortletPreferences preferences = request.getPreferences();
		
		this.useWebsocketSecured = ParamUtil.getBoolean(request, Constants.USE_WEBSOCKET_SECURED, this.useWebsocketSecured);
		this.useCustomWebsocketUrl = ParamUtil.getBoolean(request, Constants.USE_CUSTOM_WEBSOCKET_URL, this.useCustomWebsocketUrl);
		this.customWebsocketBasePath = ParamUtil.getString(request, Constants.CUSTOM_WEBSOCKET_BASE_PATH, this.customWebsocketBasePath);
		
		preferences.setValue(Constants.USE_WEBSOCKET_SECURED, String.valueOf(this.useWebsocketSecured));
		preferences.setValue(Constants.USE_CUSTOM_WEBSOCKET_URL, String.valueOf(this.useCustomWebsocketUrl));
		preferences.setValue(Constants.CUSTOM_WEBSOCKET_BASE_PATH, String.valueOf(this.customWebsocketBasePath));
		
		preferences.store();
	}

	public boolean getUseCustomWebsocketUrl() {
		return useCustomWebsocketUrl;
	}
	
	public boolean getUseWebsocketSecured() {
		return useWebsocketSecured;
	}
	
	public String getCustomWebsocketBasePath() {
		return customWebsocketBasePath;
	}

	public String getPortletId(){
		return portletId;
	}
	
	public void setUseCustomWebsocketUrl(boolean useCustomWebsocketUrl) {
		this.useCustomWebsocketUrl = useCustomWebsocketUrl;
	}

	public void setUseWebsocketSecured(boolean useWebsocketSecured) {
		this.useWebsocketSecured = useWebsocketSecured;
	}
	
	public void setCustomWebsocketBasePath(String customWebsocketBasePath) {
		this.customWebsocketBasePath = customWebsocketBasePath;
	}
	
	public void setPortletId(String portletId){
		this.portletId = portletId;
	}
}
