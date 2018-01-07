package br.com.jorchestra.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.hazelcast.core.ITopic;

import br.com.jorchestra.canonical.JOrchestraHandle;
import br.com.jorchestra.canonical.JOrchestraStateCall;
import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;

public abstract class JOrchestraWebSocketTemplate extends TextWebSocketHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraWebSocketTemplate.class);

	protected final JOrchestraHandle jOrchestraHandle;

	protected final Map<String, WebSocketSession> webSocketSessionMap = Collections
			.synchronizedMap(new HashMap<>());
	
	protected final Map<String, List<String>> jOrchestraSessionMap = Collections.synchronizedMap(new HashMap<>());
	
	protected final ITopic<JOrchestraStateCall> jOrchestraStateCallTopic;

	protected final JOrchestraConfigurationProperties jOrchestraConfigurationProperties;

	protected JOrchestraWebSocketTemplate(final JOrchestraHandle jOrchestraHandle,
			final ITopic<JOrchestraStateCall> jOrchestraStateCallTopic,
			final JOrchestraConfigurationProperties jOrchestraConfigurationProperties) {
		super();
		this.jOrchestraHandle = jOrchestraHandle;
		this.jOrchestraStateCallTopic = jOrchestraStateCallTopic;
		this.jOrchestraConfigurationProperties = jOrchestraConfigurationProperties;
	}
	
	@Override
	public boolean supportsPartialMessages() {
		return true;
	}

	@Override
	public void afterConnectionEstablished(final WebSocketSession webSocketSession) throws Exception {
		final JOrchestraStateCall jOrchestraStateCall = JOrchestraStateCall.createJOrchestraStateCall_OPEN(
				webSocketSession.getId(), jOrchestraConfigurationProperties.getClusterName(),
				jOrchestraConfigurationProperties.getName());
		LOGGER.debug("m=afterConnectionEstablished, jOrchestraStateCall=" + jOrchestraStateCall);
		webSocketSessionMap.put(webSocketSession.getId(), webSocketSession);
		jOrchestraSessionMap.put(webSocketSession.getId(), Collections.synchronizedList(new ArrayList<>()));
		jOrchestraStateCallTopic.publish(jOrchestraStateCall);
	}

	@Override
	public void handleTextMessage(final WebSocketSession webSocketSession, final TextMessage textMessage)
			throws Exception {
		final String payload = textMessage.getPayload();
		final JOrchestraStateCall jOrchestraStateCall = JOrchestraStateCall.createJOrchestraStateCall_WAITING(
				webSocketSession.getId(), jOrchestraConfigurationProperties.getClusterName(),
				jOrchestraConfigurationProperties.getName(), payload);
		LOGGER.debug("m=handleTextMessage, jOrchestraStateCall=" + jOrchestraStateCall + ", payload="
				+ textMessage.getPayload());

		jOrchestraSessionMap.get(webSocketSession.getId()).add(jOrchestraStateCall.getId());
		jOrchestraStateCallTopic.publish(jOrchestraStateCall);
	}

	@Override
	public void afterConnectionClosed(final WebSocketSession webSocketSession, final CloseStatus closeStatus)
			throws Exception {
		final JOrchestraStateCall jOrchestraStateCall = JOrchestraStateCall.createJOrchestraStateCall_CLOSE(
				webSocketSession.getId(), jOrchestraConfigurationProperties.getClusterName(),
				jOrchestraConfigurationProperties.getName());
		LOGGER.debug(
				"m=afterConnectionClosed, jOrchestraStateCall=" + jOrchestraStateCall + ", closeStatus=" + closeStatus);
		jOrchestraSessionMap.get(webSocketSession.getId()).clear();
		jOrchestraSessionMap.remove(webSocketSession.getId());
		webSocketSessionMap.remove(webSocketSession.getId());
		jOrchestraStateCallTopic.publish(jOrchestraStateCall);
	}

	@Override
	public void handleTransportError(final WebSocketSession webSocketSession, final Throwable e) throws Exception {
		final JOrchestraStateCall jOrchestraStateCall = JOrchestraStateCall.createJOrchestraStateCall_ERROR(
				webSocketSession.getId(), jOrchestraConfigurationProperties.getClusterName(),
				jOrchestraConfigurationProperties.getName());
		LOGGER.error("m=handleTransportError, jOrchestraStateCall=" + jOrchestraStateCall, e);
		jOrchestraStateCallTopic.publish(jOrchestraStateCall);
	}
}
