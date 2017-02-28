package com.rivetlogic.whiteboard.realtime;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;

public class WhiteboardUtil {
	public static final String SESSIONID = "sessionId";
    public static final String GUEST_USER_NAME_LABEL = "rivetlogic.whiteboard.guest.name.label";
    public static final String LOGGED_USERS_MAP_KEY = "rivetlogic.whiteboard.logged.users.map";
    public static final String WHITEBOARD_DUMP_KEY = "rivetlogic.whiteboard.dump.map";
    public static final String WHITEBOARD_SESSIONS_KEY = "rivetlogic.whiteboard.sessions.map";
    /* ACTIONS */
    public static final String CREATE = "create";
    public static final String DELETE = "delete";
    public static final String LOGIN = "login";

    /* JSON PROPERTIES */
    public static final String STATE = "state";
    public static final String OPTIONS = "options";
    public static final String RADIUS = "radius";
    public static final String USERS = "users";
    public static final String TYPE = "type";
    public static final String USERNAME = "userName";
    public static final String BASE_IMAGEPATH = "baseImagePath";
    public static final String USER_IMAGEPATH = "userImagePath";
    public static final String COMMANDS = "commands";
    public static final String CACHEID = "cacheId";
    public static final String ACTION = "action";
    public static final String TOOLTIP = "tooltip";
    public static final String EDITOR_ID = "editorId";
    public static final String DEFAULT_EDITOR_ID = "0";

    /* SHAPES */
    public static final String LINE = "line";
    public static final String CIRCLE = "circle";
    public static final String PATH = "path";
    public static final int CIRCLE_RADIUS = 20; // circle radius needs to be set
                                                // because js cant retrieve the
                                                // circle radius from the shape

    private static final Log LOG = LogFactoryUtil.getLog(WhiteboardUtil.class);
    
    /**
     * Generate JSON from current logged users map.
     * 
     * @param loggedUserMap
     * @return
     */
    public static JSONObject generateLoggedUsersJSON(ConcurrentMap<String, UserData> loggedUserMap) {
        JSONObject usersLogged = JSONFactoryUtil.createJSONObject();
        JSONObject usersUpdateCommand = JSONFactoryUtil.createJSONObject();
        JSONArray commands = JSONFactoryUtil.createJSONArray();
        JSONArray users = JSONFactoryUtil.createJSONArray();

        usersUpdateCommand.put(ACTION, USERS);

        for (Entry<String, UserData> entry : loggedUserMap.entrySet()) {
            String key = entry.getKey();
            UserData userData = entry.getValue();
            JSONObject user = JSONFactoryUtil.createJSONObject();
            LOG.debug(user);
            user.put(USERNAME, userData.getUserName());
            user.put(USER_IMAGEPATH, userData.getUserImagePath());
            user.put(SESSIONID, key);
            users.put(user);
        }

        usersUpdateCommand.put(USERS, users);

        /* add to commands */
        commands.put(usersUpdateCommand);

        /* add commands to main json */
        usersLogged.put(COMMANDS, commands);
        usersLogged.put(EDITOR_ID, DEFAULT_EDITOR_ID);

        LOG.debug(usersLogged.toString());

        return usersLogged;

    }
    
    /**
     * Transforms current dump into JSON array of actions to be dumped.
     * 
     * @param whiteBoardDump
     * @return
     */
    public static JSONArray loadWhiteboardDump(ConcurrentMap<String, JSONObject> whiteBoardDump) {

        JSONArray dump = JSONFactoryUtil.createJSONArray();
        for (Entry<String, JSONObject> entry : whiteBoardDump.entrySet()) {
            JSONObject command = entry.getValue();
            dump.put(command);
        }
        return dump;

    }
    
    public static User getUser(long userId) {
    	try {
			return UserLocalServiceUtil.getUser(Long.valueOf(userId));
		} catch (PortalException e) {
			e.printStackTrace();
		}
    	return null;
    }
}
