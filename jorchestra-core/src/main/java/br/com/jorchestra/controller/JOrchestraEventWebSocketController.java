package br.com.jorchestra.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import br.com.jorchestra.dto.JOrchestraSystemEvent;
import br.com.jorchestra.handle.JOrchestraHandle;

public class JOrchestraEventWebSocketController extends TextWebSocketHandler
		implements MessageListener<JOrchestraSystemEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraEventWebSocketController.class);

	private final Map<String, WebSocketSession> webSocketSessionMap = Collections.synchronizedMap(new HashMap<>());

	private final JOrchestraHandle jOrchestraHandle;

	public JOrchestraEventWebSocketController(final JOrchestraHandle jOrchestraHandle) {
		this.jOrchestraHandle = jOrchestraHandle;
	}

	@Override
	public void onMessage(final Message<JOrchestraSystemEvent> message) {
		webSocketSessionMap.entrySet().parallelStream().forEach(action -> {
			try {
				sendEnvent(action.getValue(), message.getMessageObject());
			} catch (IOException e) {
				LOGGER.error("m=onMessage, message=" + message, e);
			}
		});
	}

	@Override
	public void afterConnectionEstablished(final WebSocketSession webSocketSession) throws Exception {
		final String sessionId = webSocketSession.getId();
		final UUID requestId = UUID.randomUUID();
		final String logMsg = "m=afterConnectionEstablished, webSocketSession.id=" + sessionId + ", requestId="
				+ requestId.toString() + ", path=" + jOrchestraHandle.getPath() + ", method="
				+ jOrchestraHandle.getMethod();

		LOGGER.debug(logMsg);
		webSocketSessionMap.put(sessionId, webSocketSession);
	}

	@Override
	public void afterConnectionClosed(final WebSocketSession webSocketSession, final CloseStatus closeStatus)
			throws Exception {
		final String sessionId = webSocketSession.getId();
		final String reason = closeStatus.getReason();
		final Integer code = closeStatus.getCode();

		final UUID requestId = UUID.randomUUID();
		final String logMsg = "m=afterConnectionClosed, webSocketSession.id=" + sessionId + ", closeStatus.reason="
				+ reason + ", closeStatus.code=" + code + ", requestId=" + requestId + ", path="
				+ jOrchestraHandle.getPath() + ", method=" + jOrchestraHandle.getMethod();

		LOGGER.debug(logMsg);
		webSocketSessionMap.remove(sessionId);
	}

	@Override
	public void handleTransportError(final WebSocketSession webSocketSession, final Throwable e) throws Exception {
		final String sessionId = webSocketSession.getId();
		final UUID requestId = UUID.randomUUID();

		final String logMsg = "m=afterConnectionClosed, webSocketSession.id=" + sessionId + ", requestId=" + requestId
				+ ", path=" + jOrchestraHandle.getPath() + ", method=" + jOrchestraHandle.getMethod();

		LOGGER.debug(logMsg, e);
		webSocketSession.close(CloseStatus.SERVER_ERROR);
	}

	private void sendEnvent(final WebSocketSession webSocketSession, final Object object)
			throws IOException, JsonProcessingException {
		final ObjectMapper objectMapper = new ObjectMapper();
		final String payload = objectMapper.writeValueAsString(object);
		LOGGER.debug("m=sendCallback, payload=" + payload);
		webSocketSession.sendMessage(new TextMessage(payload));
	}
}
