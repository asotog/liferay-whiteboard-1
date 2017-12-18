package com.rivetlogic.whiteboard.portlet;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ValidatorException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

//import aQute.bnd.annotation.metatype.Configurable;

/**
 * @author alejandrosoto
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.collaboration",
		"com.liferay.portlet.instanceable=false",
		"com.liferay.portlet.css-class-wrapper=whiteboard-portlet",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.header-portlet-css=/css/main-responsive.css",
		"javax.portlet.portlet-mode=text/html;VIEW,EDIT",
		"javax.portlet.name=" + Constants.PORTLET_ID,
		"javax.portlet.display-name=Collaboration Whiteboard",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.init-param.edit-jsp=/edit.jsp",
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class,
	configurationPid = "com.rivetlogic.whiteboard.portlet.Preferences"
)
public class CollaborationWhiteboardPortlet extends MVCPortlet {
	private volatile Preferences preferences;
	
	@Override
    public void render(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        PreferencesBean prefBean = new PreferencesBean(request, 
            getUseCustomWebsocketUrl(), 
            getUseWebsocketSecured(),
            preferences.getCustomWebsocketBasePath());
        request.setAttribute(Constants.SIGNED_IN, themeDisplay.isSignedIn());
        request.setAttribute(Constants.PREF_BEAN, prefBean);
        super.render(request, response);
    }
	
	public void savePreferences(ActionRequest request, ActionResponse response) throws ReadOnlyException, ValidatorException, IOException{
        PreferencesBean prefBean = new PreferencesBean();
        prefBean.save(request);
        SessionMessages.add(request, PortalUtil.getPortletId(request) +
                SessionMessages.KEY_SUFFIX_UPDATED_PREFERENCES);

        String redirect = getRedirect(request, response);

        if (Validator.isNotNull(redirect)) {
            response.sendRedirect(redirect);
        }
    }
	
	private boolean getUseCustomWebsocketUrl() {
		return Boolean.valueOf(preferences.getUseCustomWebsocketUrl());
    }
	
	private boolean getUseWebsocketSecured() {
		return Boolean.valueOf(preferences.getUseWebsocketSecured());
	}
	
	@Activate
    @Modified
    protected void activate(Map<String, Object> properties) {
		preferences = ConfigurableUtil.createConfigurable(Preferences.class, properties);
    }
}