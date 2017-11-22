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

package com.rivetlogic.portlet.whiteboard.atmosphere;

import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserConstants;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.config.service.Singleton;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.handler.AtmosphereHandlerAdapter;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.BroadcastOnPostAtmosphereInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;
import org.atmosphere.util.SimpleBroadcaster;

@Singleton

@AtmosphereHandlerService(  path = "/", supportSession = true,
                            interceptors = {
                                AtmosphereResourceLifecycleInterceptor.class,
                                TrackMessageSizeInterceptor.class,
                                BroadcastOnPostAtmosphereInterceptor.class,
                                SuspendTrackerInterceptor.class },
                            broadcaster = SimpleBroadcaster.class)

public class WhiteboardHandler extends AtmosphereHandlerAdapter {

    public static final String CACHE_NAME = WhiteboardHandler.class.getName();
    private static final String ENCODING = "UTF-8";
    private static final String DUMP_MESSAGE = "dump";
    private static final Log LOG = LogFactoryUtil.getLog(WhiteboardHandler.class);
    @SuppressWarnings("rawtypes")
    private static PortalCache portalCache = MultiVMPoolUtil.getCache(CACHE_NAME);

    @SuppressWarnings("unchecked")
    private ConcurrentMap<String, UserData> getLoggedUsersMap() {

        Object object = portalCache.get(WhiteboardHandlerUtil.LOGGED_USERS_MAP_KEY);
        ConcurrentMap<String, UserData> loggedUserMap = (ConcurrentMap<String, UserData>) object;
        if (null == loggedUserMap) {
            loggedUserMap = new ConcurrentSkipListMap<String, UserData>();
            portalCache.put(WhiteboardHandlerUtil.LOGGED_USERS_MAP_KEY, loggedUserMap);
        }
        return loggedUserMap;
    }

    @SuppressWarnings("unchecked")
    private ConcurrentMap<String, JSONObject> getWhiteBoardDump() {
        Object object = portalCache.get(WhiteboardHandlerUtil.WHITEBOARD_DUMP_KEY);
        ConcurrentMap<String, JSONObject> whiteBoardDump = (ConcurrentMap<String, JSONObject>) object;
        if (null == whiteBoardDump) {
            whiteBoardDump = new ConcurrentSkipListMap<String, JSONObject>();
            portalCache.put(WhiteboardHandlerUtil.WHITEBOARD_DUMP_KEY, whiteBoardDump);
        }
        return whiteBoardDump;
    }

    @Override
    public void onRequest(AtmosphereResource resource) throws IOException {

        ConcurrentMap<String, UserData> loggedUserMap = getLoggedUsersMap();

        String userName = StringPool.BLANK;
        String userImagePath = StringPool.BLANK;

        // user joined
        String sessionId = resource.session().getId();
        if (loggedUserMap.get(sessionId) == null) {

            try {

                String baseImagePath = URLDecoder.decode(
                        resource.getRequest().getParameter(WhiteboardHandlerUtil.BASE_IMAGEPATH), ENCODING);
                //LOG.debug("base image path " + baseImagePath);

                User user = PortalUtil.getUser(resource.getRequest());
                long companyId = PortalUtil.getCompanyId(resource.getRequest());

                if (user == null || user.isDefaultUser()) {
                    //LOG.debug("This is guest user");
                    user = UserLocalServiceUtil.getDefaultUser(companyId);
                    userName = LanguageUtil.get(LocaleUtil.getDefault(), WhiteboardHandlerUtil.GUEST_USER_NAME_LABEL);
                } else {
                    userName = user.getFullName();
                }

                userImagePath = UserConstants.getPortraitURL(baseImagePath, user.isMale(), user.getPortraitId());

                //LOG.debug(String.format("User full name: %s, User image path: %s", userName, userImagePath));
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }

            loggedUserMap.put(resource.session().getId(), new UserData(userName, userImagePath));

            /* listens to disconnection event */
            resource.addEventListener(new WhiteBoardResourceEventListener(loggedUserMap, sessionId));
        }
    }

    @Override
    public void onStateChange(AtmosphereResourceEvent event) throws IOException {

        ConcurrentMap<String, UserData> loggedUserMap = getLoggedUsersMap();
        ConcurrentMap<String, JSONObject> whiteBoardDump = getWhiteBoardDump();

        /* messages broadcasting */
        if (event.isSuspended()) {
            String message = event.getMessage() == null ? StringPool.BLANK : event.getMessage().toString();

            if (!message.equals(StringPool.BLANK)) {

                try {
                    JSONObject jsonMessage = JSONFactoryUtil.createJSONObject(message);
                    /* verify if user is signing in */
                    if (WhiteboardHandlerUtil.LOGIN.equals(jsonMessage.getString(WhiteboardHandlerUtil.TYPE))) {
                        JSONObject usersLoggedMessage = WhiteboardHandlerUtil.generateLoggedUsersJSON(loggedUserMap);
                        /* adds whiteboard dump to the message */
                        usersLoggedMessage.put(DUMP_MESSAGE, WhiteboardHandlerUtil.loadWhiteboardDump(whiteBoardDump));
                        event.getResource().getBroadcaster().broadcast(usersLoggedMessage);
                    } else {
                        /* just broadcast the message */
                        //LOG.debug("Broadcasting = " + message);
                        /* adds whiteboard updates to the dump */
                        WhiteboardHandlerUtil.persistWhiteboardDump(whiteBoardDump, jsonMessage);
                        event.getResource().write(message);
                    }
                } catch (JSONException e) {
                    LOG.debug("JSON parse failed");
                }
            }
        }
    }
}
