package com.rivetlogic.whiteboard.portlet;

import aQute.bnd.annotation.metatype.Meta;

@Meta.OCD(id = "com.rivetlogic.whiteboard.portlet.Preferences")
public interface Preferences {

	@Meta.AD(deflt = "false", required = false)
    public String getUseWebsocketSecured();
	
    @Meta.AD(deflt = "false", required = false)
    public String getUseCustomWebsocketUrl();
    
    @Meta.AD(deflt = "", required = false)
    public String getCustomWebsocketBasePath();
}

