package br.com.jorchestra.client;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.jorchestra.client.exception.JOrchestraConnectionCloseException;
import br.com.jorchestra.client.exception.JOrchestraConnectionErrorException;
import br.com.jorchestra.dto.JOrchestraBeanResponse;

@ClientEndpoint
public final class JOrchestraBeanClient implements Closeable{

	protected static JOrchestraBeanClient create() {
		return new JOrchestraBeanClient();
	}

	private final ObjectMapper objectMapper = new ObjectMapper();

	private String sessionId;
	private UUID uuid;
	private volatile Boolean receiveData = Boolean.FALSE;
	private CloseReason closeReason;
	private Throwable t;

	private JOrchestraBeanClient() {
	}

	protected List<JOrchestraBeanResponse> sendMessage() throws InterruptedException, ExecutionException,
			JOrchestraConnectionCloseException, JOrchestraConnectionErrorException {

		if (closeReason != null) {
			throw new JOrchestraConnectionCloseException(closeReason);
		}

		if (t != null) {
			throw new JOrchestraConnectionErrorException(t);
		}

		final Future<Void> future = JOrchestraSessionManager.getSession(sessionId).getAsyncRemote().sendText("");
		future.get();

		while (!receiveData)
			;

		receiveData = Boolean.FALSE;
		final String resutPayload = JOrchestraSessionManager.removeMessage(uuid);

		try {
			return Arrays.asList(objectMapper.readValue(resutPayload, JOrchestraBeanResponse[].class));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@OnMessage
	public void onMessage(final String message) throws JsonParseException, JsonMappingException, IOException {
		this.uuid = JOrchestraSessionManager.addMessage(message);
		this.receiveData = Boolean.TRUE;
	}

	@OnOpen
	public void onOpen(final Session session) throws IOException {
		this.sessionId = JOrchestraSessionManager.addSession(session);
	}

	@OnClose
	public void onClose(final Session session, final CloseReason closeReason) {
		this.closeReason = closeReason;
		JOrchestraSessionManager.removeMessage(uuid);
		JOrchestraSessionManager.removeSession(session.getId());
	}

	@OnError
	public void onError(final Session session, final Throwable t) {
		this.t = t;
		JOrchestraSessionManager.removeMessage(uuid);
		JOrchestraSessionManager.removeSession(session.getId());
	}

	@Override
	public void close() throws IOException {
		JOrchestraSessionManager.removeMessage(uuid);
		JOrchestraSessionManager.removeSession(sessionId).close();
	}
}
