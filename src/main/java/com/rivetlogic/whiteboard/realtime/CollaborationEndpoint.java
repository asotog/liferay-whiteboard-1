package com.rivetlogic.whiteboard.realtime;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.osgi.service.component.annotations.Component;

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
	private Map<String, Session> sessions; // opened sessions map
	
	public CollaborationEndpoint() {
		super();
		sessions = new ConcurrentHashMap<>();
	}
	
	@Override
	public void onOpen(Session session, EndpointConfig config) {
		Map<String, String[]> parameters = HttpUtil.getParameterMap(session.getQueryString());
		String userId = parameters.get("userId")[0];
		sessions.put(session.getId(), session);
		
		/* adds message handler on current opened session */
		session.addMessageHandler(new MessageHandler.Whole<String>() {

			@Override
			public void onMessage(String text) {
				broadcast(text, sessions);
			}

		});
	}
	
	@Override
	public void onClose(Session session, CloseReason closeReason) {
		super.onClose(session, closeReason);
		sessions.remove(session.getId()); // gets rid from the sessions map of current session
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

}
