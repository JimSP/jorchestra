package br.com.jorchestra.controller;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.jorchestra.canonical.JOrchestraStateCall;
import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
import br.com.jorchestra.dto.JOrchestraAdminRequest;
import br.com.jorchestra.runtime.JOrchestraRuntime;
import br.com.jorchestra.runtime.RuntimeCallback;

public final class JOrchestraAdminWebSocket extends TextWebSocketHandler implements RuntimeCallback {
	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraAdminWebSocket.class);

	private final JOrchestraConfigurationProperties jOrchestraConfigurationProperties;
	private final Map<String, Map<JOrchestraStateCall, Future<Object>>> executorServiceMap;

	public JOrchestraAdminWebSocket(final JOrchestraConfigurationProperties jOrchestraConfigurationProperties,
			Map<String, Map<JOrchestraStateCall, Future<Object>>> executorServiceMap) {
		this.jOrchestraConfigurationProperties = jOrchestraConfigurationProperties;
		this.executorServiceMap = executorServiceMap;
	}

	@Override
	public boolean supportsPartialMessages() {
		return jOrchestraConfigurationProperties.getSupportsPartialMessages();
	}

	@Override
	public void afterConnectionEstablished(final WebSocketSession webSocketSession) throws Exception {
		LOGGER.debug("m=afterConnectionEstablished, sessionId=" + webSocketSession.getId());
	}

	@Override
	public void handleTextMessage(final WebSocketSession webSocketSession, final TextMessage textMessage)
			throws Exception {
		final String payload = textMessage.getPayload();
		LOGGER.debug("m=handleTextMessage, sessionId=" + webSocketSession.getId() + ", payload=" + payload);

		final ObjectMapper objectMapper = new ObjectMapper();
		final JOrchestraAdminRequest jOrchestraAdminRequest = objectMapper.readValue(payload,
				JOrchestraAdminRequest.class);

		if (JOrchestraAdminRequest.isValidUserNameAndPassword(jOrchestraAdminRequest,
				jOrchestraConfigurationProperties)) {
			jOrchestraAdminRequest.getJOrchestraCommand().execute(this, executorServiceMap,
					jOrchestraConfigurationProperties, jOrchestraAdminRequest, webSocketSession,
					new JOrchestraRuntime());
		} else {
			sendMessage(webSocketSession, "authentication fail.", false);
		}
	}

	public void sendMessage(final WebSocketSession webSocketSession, final String tag, final Object... result) {
		try {
			final ObjectMapper objectMapper = new ObjectMapper();
			final String payload = objectMapper.writeValueAsString(result);
			webSocketSession.sendMessage(new TextMessage(payload));
		} catch (IOException e) {
			LOGGER.error("m=sendMessage, sessionId=" + webSocketSession.getId());
			throw new RuntimeException("m=sendMessage, sessionId=" + webSocketSession.getId(), e);
		}
	}

	@Override
	public void afterConnectionClosed(final WebSocketSession webSocketSession, final CloseStatus closeStatus)
			throws Exception {
		LOGGER.debug("m=afterConnectionClosed, sessionId=" + webSocketSession.getId() + ", closeStatus=" + closeStatus);
	}

	@Override
	public void handleTransportError(final WebSocketSession webSocketSession, final Throwable e) throws Exception {
		LOGGER.error("m=handleTransportError, sessionId=" + webSocketSession.getId(), e);
	}
}
