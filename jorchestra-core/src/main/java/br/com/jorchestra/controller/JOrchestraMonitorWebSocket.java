package br.com.jorchestra.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

import br.com.jorchestra.canonical.JOrchestraStateCall;
import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;

public final class JOrchestraMonitorWebSocket extends TextWebSocketHandler
		implements MessageListener<JOrchestraStateCall> {

	private final Map<String, WebSocketSession> webSocketSessionMap = Collections.synchronizedMap(new HashMap<>());

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraMonitorWebSocket.class);

	private final JOrchestraConfigurationProperties jOrchestraConfigurationProperties;

	public JOrchestraMonitorWebSocket(final JOrchestraConfigurationProperties jOrchestraConfigurationProperties) {
		this.jOrchestraConfigurationProperties = jOrchestraConfigurationProperties;
	}

	@Override
	public boolean supportsPartialMessages() {
		return jOrchestraConfigurationProperties.getSupportsPartialMessages();
	}

	@Override
	public void onMessage(final Message<JOrchestraStateCall> message) {
		try {
			sendEnvent(message.getMessageObject());
		} catch (IOException e) {
			LOGGER.error("m=onMessage, jOrchestraStateCall=" + message.getMessageObject());
		}
	}

	@Override
	public void afterConnectionEstablished(final WebSocketSession webSocketSession) throws Exception {
		webSocketSessionMap.put(webSocketSession.getId(), webSocketSession);
	}

	@Override
	public void afterConnectionClosed(final WebSocketSession webSocketSession, final CloseStatus closeStatus)
			throws Exception {
		webSocketSessionMap.remove(webSocketSession.getId());
	}

	private void sendEnvent(final JOrchestraStateCall jOrchestraStateCall) throws IOException, JsonProcessingException {
		final ObjectMapper objectMapper = new ObjectMapper();
		final String payload = objectMapper.writeValueAsString(jOrchestraStateCall);

		webSocketSessionMap //
				.entrySet() //
				.parallelStream() //
				.forEach(action -> {
					try {
						action.getValue().sendMessage(new TextMessage(payload));
					} catch (IOException e) {
						LOGGER.error("m=sendEnvent, jOrchestraStateCall=" + jOrchestraStateCall);
					}
				});

	}
}
