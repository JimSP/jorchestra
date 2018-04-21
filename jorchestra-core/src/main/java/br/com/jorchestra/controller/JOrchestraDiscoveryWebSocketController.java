package br.com.jorchestra.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;

import br.com.jorchestra.canonical.DiscroveryEventType;
import br.com.jorchestra.canonical.JOrchestraHandle;
import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
import br.com.jorchestra.util.JOrchestraContextUtils;

public class JOrchestraDiscoveryWebSocketController extends TextWebSocketHandler
		implements ItemListener<JOrchestraHandle> {

	private final Map<String, WebSocketSession> WebSocketSessionPoll = Collections.synchronizedMap(new HashMap<>());

	private JOrchestraConfigurationProperties jOrchestraConfigurationProperties;

	public JOrchestraDiscoveryWebSocketController(
			final JOrchestraConfigurationProperties jOrchestraConfigurationProperties) {
		this.jOrchestraConfigurationProperties = jOrchestraConfigurationProperties;
	}

	@Override
	public boolean supportsPartialMessages() {
		return jOrchestraConfigurationProperties.getSupportsPartialMessages();
	}

	@Override
	public void afterConnectionEstablished(final WebSocketSession webSocketSession) throws Exception {

		WebSocketSessionPoll.put(webSocketSession.getId(), webSocketSession);

		final HazelcastInstance hazelcastInstance = JOrchestraContextUtils.getJOrchestraHazelcastInstance();
		final ISet<JOrchestraHandle> jOrchestraPathRegisterSet = hazelcastInstance.getSet("jOrchestraPathRegisterSet");
		jOrchestraPathRegisterSet //
				.forEach(action -> {
					try {
						webSocketSession //
								.sendMessage(createPayload(DiscroveryEventType.ADD, action));
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
	}

	@Override
	public void afterConnectionClosed(final WebSocketSession webSocketSession, final CloseStatus closeStatus)
			throws Exception {
		WebSocketSessionPoll.remove(webSocketSession.getId());
	}

	private void sendEnvent(final DiscroveryEventType discroveryEventType, final JOrchestraHandle jOrchestraHandle) {
		synchronized (WebSocketSessionPoll) {
			final Iterator<Entry<String, WebSocketSession>> iterator = WebSocketSessionPoll.entrySet().iterator();
			while (iterator.hasNext()) {
				try {
					iterator.next().getValue().sendMessage(createPayload(discroveryEventType, jOrchestraHandle));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private TextMessage createPayload(final DiscroveryEventType discroveryEventType,
			final JOrchestraHandle jOrchestraHandle) throws JsonProcessingException {
		final ObjectMapper objectMapper = new ObjectMapper();

		return new TextMessage(objectMapper.writeValueAsString(new Object[] { discroveryEventType, jOrchestraHandle }));
	}

	@Override
	public void itemAdded(final ItemEvent<JOrchestraHandle> item) {
		sendEnvent(DiscroveryEventType.ADD, item.getItem());
	}

	@Override
	public void itemRemoved(final ItemEvent<JOrchestraHandle> item) {
		sendEnvent(DiscroveryEventType.REMOVE, item.getItem());
	}
}
