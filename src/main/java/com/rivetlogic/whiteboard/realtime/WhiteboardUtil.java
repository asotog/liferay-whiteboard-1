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
    
    /**
     * Persists whiteboard state, so if new users join the whiteboard, they can
     * use the dump to initialize the whiteboard with the shapes previously
     * created.
     * 
     * @param whiteBoardDump
     * @param jsonMessage
     */
    public static void persistWhiteboardDump(ConcurrentMap<String, JSONObject> whiteBoardDump, JSONObject jsonMessage) {

        if (jsonMessage.getJSONArray(COMMANDS) != null) {
            JSONArray messageCommands = jsonMessage.getJSONArray(COMMANDS);
            int commandsLength = messageCommands.length();
            for (int i = 0; i < commandsLength; i++) {
                JSONObject command = messageCommands.getJSONObject(i);

                if (!command.getString(ACTION).equals(USERS) && !command.getString(ACTION).equals(TOOLTIP)) {
                    /* if action is a delete command, removes entry from dump */
                    if (command.getString(ACTION).equals(DELETE)) {
                        whiteBoardDump.remove(command.getString(CACHEID));
                    } else {
                        /*
                         * if shape already exists in dump map re sets the map
                         * entry
                         */
                        if (whiteBoardDump.get(command.getString(CACHEID)) != null) {
                            JSONObject cachedCommand = whiteBoardDump.get(command.getString(CACHEID));
                            if (command.getString(TYPE).equals(LINE)) {

                                cachedCommand.getJSONObject(STATE).put(OPTIONS, command.getJSONObject(STATE));

                            } else if (command.getString(TYPE).equals(CIRCLE)) {

                                cachedCommand.put(STATE, command.getJSONObject(STATE));
                                cachedCommand.getJSONObject(STATE).put(RADIUS, CIRCLE_RADIUS);

                            } else if (command.getString(TYPE).equals(PATH)) {

                                cachedCommand.getJSONObject(STATE).put(OPTIONS,
                                        command.getJSONObject(STATE).getJSONObject(OPTIONS));

                            } else {

                                cachedCommand.put(STATE, command.getJSONObject(STATE));

                            }
                            command = cachedCommand;
                        }
                        whiteBoardDump.put(command.getString(CACHEID), command);
                    }
                }
            }
        }
    }
    
    /**
     * Gets Liferay User by userId
     * 
     * @param userId {long}
     * @return User
     */
    public static User getUser(long userId) {
    	try {
			return UserLocalServiceUtil.getUser(Long.valueOf(userId));
		} catch (PortalException e) {
			e.printStackTrace();
		}
    	return null;
    }
}
