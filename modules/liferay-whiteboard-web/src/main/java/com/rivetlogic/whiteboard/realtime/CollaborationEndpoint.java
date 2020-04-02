package com.rivetlogic.whiteboard.realtime;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.HttpUtil;

@Component(
		immediate = true,
		property = {
			"org.osgi.http.websocket.endpoint.path=" + CollaborationEndpoint.PATH
		},
		service = Endpoint.class
	)
public class CollaborationEndpoint extends Endpoint {
	
	public static final String PATH = "/o/collaboration-whiteboard";
	
	//private Map<String, Session> sessions; // opened sessions map
	 
	public static final String CACHE_NAME = CollaborationEndpoint.class.getName();
	@SuppressWarnings("rawtypes")
	private static PortalCache portalCache = MultiVMPoolUtil.getPortalCache(CACHE_NAME);
	
	private static final String DUMP_MESSAGE = "dump"; // a dump messsage is when new user comes and needs to receive the current whiteboard shapes created/stored
	private static final Log LOG = LogFactoryUtil.getLog(CollaborationEndpoint.class);
	
	public CollaborationEndpoint() {
		super();
		//sessions = new ConcurrentHashMap<>();
	}
	
	@Override
	public void onOpen(Session session, EndpointConfig config) {
		// connection url query string parameters map
		Map<String, String[]> parameters = HttpUtil.getParameterMap(session.getQueryString());
		// user parameters
		String userId = parameters.get("userId")[0];
		String userImagePath = parameters.get("userImagePath")[0];
		String guestLabel = parameters.get("guestLabel")[0];
		// currentUser {User}
		User currentUser = WhiteboardUtil.getUser(Long.valueOf(userId));
		// data maps
		ConcurrentMap<String, UserData> loggedUserMap = getLoggedUsersMap();
		ConcurrentMap<String, Session> sessions = getSessions();
		
		String userName = "";
		
		if (sessions.get(session.getId()) == null && currentUser != null) {
			if (currentUser.isDefaultUser()) {
                LOG.debug("This is guest user");
                userName = guestLabel;
            } else {
                userName = currentUser.getFullName();
            }
			sessions.put(session.getId(), session);
			loggedUserMap.put(session.getId(), new UserData(userName, userImagePath));
			
			LOG.debug(String.format("User full name: %s, User image path: %s", userName, userImagePath));
			
			/* adds message handler on current opened session */
			session.addMessageHandler(new MessageHandler.Whole<String>() {

				@Override
				public void onMessage(String text) {
					onMessageHandler(text);
				}

			});
		}
	}
	
	private void onMessageHandler(String text) {
		ConcurrentMap<String, JSONObject> whiteBoardDump = getWhiteBoardDump();
		ConcurrentMap<String, UserData> loggedUserMap = getLoggedUsersMap();
		ConcurrentMap<String, Session> sessions = getSessions();
		
		try {
			JSONObject jsonMessage = JSONFactoryUtil.createJSONObject(text);
			if (WhiteboardUtil.LOGIN.equals(jsonMessage.getString(WhiteboardUtil.TYPE))) {
				JSONObject usersLoggedMessage = WhiteboardUtil.generateLoggedUsersJSON(loggedUserMap);
                /* adds whiteboard dump to the message */
                usersLoggedMessage.put(DUMP_MESSAGE, WhiteboardUtil.loadWhiteboardDump(whiteBoardDump));
                broadcast(usersLoggedMessage.toString(), sessions);
			} else {
				/* just broadcast the message */
                LOG.debug("Broadcasting = " + text);
                /* adds whiteboard updates to the dump */
                WhiteboardUtil.persistWhiteboardDump(whiteBoardDump, jsonMessage);
				broadcast(text, sessions);
			}
			
		} catch (JSONException e) {
			LOG.debug("JSON parse failed");
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClose(Session session, CloseReason closeReason) {
		super.onClose(session, closeReason);
		ConcurrentMap<String, UserData> loggedUserMap = getLoggedUsersMap();
		ConcurrentMap<String, Session> sessions = getSessions();
		
		loggedUserMap.remove(session.getId()); // gets rid from the sessions users info map
		sessions.remove(session.getId()); // gets rid from the sessions map
		
		broadcast(WhiteboardUtil.generateLoggedUsersJSON(loggedUserMap).toString(), sessions);
	}
	
	/**
	 * Sends message to every opened session
	 * 
	 * @param message
	 * @param sessions
	 */
	private void broadcast(String message, Map<String, Session> sessions) {
		try {
			for (Session session : sessions.values()) {
				session.getBasicRemote().sendText(message);
			}
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	
	@SuppressWarnings("unchecked")
	private ConcurrentMap<String, UserData> getLoggedUsersMap() {
        Object object = portalCache.get(WhiteboardUtil.LOGGED_USERS_MAP_KEY);
        ConcurrentMap<String, UserData> loggedUserMap = (ConcurrentMap<String, UserData>) object;
        if (null == loggedUserMap) {
            loggedUserMap = new ConcurrentSkipListMap<String, UserData>();
            portalCache.put(WhiteboardUtil.LOGGED_USERS_MAP_KEY, loggedUserMap);
        }
        return loggedUserMap;
    }
	
	@SuppressWarnings("unchecked")
    private ConcurrentMap<String, JSONObject> getWhiteBoardDump() {
        Object object = portalCache.get(WhiteboardUtil.WHITEBOARD_DUMP_KEY);
        ConcurrentMap<String, JSONObject> whiteBoardDump = (ConcurrentMap<String, JSONObject>) object;
        if (null == whiteBoardDump) {
            whiteBoardDump = new ConcurrentSkipListMap<String, JSONObject>();
            portalCache.put(WhiteboardUtil.WHITEBOARD_DUMP_KEY, whiteBoardDump);
        }
        return whiteBoardDump;
    }
	
	@SuppressWarnings("unchecked")
    private ConcurrentMap<String, Session> getSessions() {
        Object object = portalCache.get(WhiteboardUtil.WHITEBOARD_SESSIONS_KEY);
        ConcurrentMap<String, Session> sessions = (ConcurrentMap<String, Session>) object;
        if (null == sessions) {
        	sessions = new ConcurrentSkipListMap<String, Session>();
            portalCache.put(WhiteboardUtil.WHITEBOARD_SESSIONS_KEY, sessions);
        }
        return sessions;
    }

}
