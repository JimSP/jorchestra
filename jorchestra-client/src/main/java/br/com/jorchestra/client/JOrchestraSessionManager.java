package br.com.jorchestra.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.websocket.Session;

public final class JOrchestraSessionManager {

	private static final class Singleton {
		private static JOrchestraSessionManager jOrchestraSessionManagerInstance = new JOrchestraSessionManager();
	}

	private final Map<String, Session> sessionPool = Collections.synchronizedMap(new HashMap<>());
	private final Map<UUID, String> messagePoll = Collections.synchronizedMap(new HashMap<>());

	private JOrchestraSessionManager() {

	}

	public static String addSession(final Session session) {
		Singleton.jOrchestraSessionManagerInstance.sessionPool.put(session.getId(), session);
		return session.getId();
	}

	public static Session getSession(final String sessionId) {
		return Singleton.jOrchestraSessionManagerInstance.sessionPool.get(sessionId);
	}

	public static Session removeSession(final String sessionId) {
		return Singleton.jOrchestraSessionManagerInstance.sessionPool.remove(sessionId);
	}

	public static UUID addMessage(final String message) {
		final UUID uuid = UUID.randomUUID();
		Singleton.jOrchestraSessionManagerInstance.messagePoll.put(uuid, message);
		return uuid;
	}

	public static String removeMessage(final UUID uuid) {
		return Singleton.jOrchestraSessionManagerInstance.messagePoll.remove(uuid);
	}
}
