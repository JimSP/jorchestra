package br.com.jorchestra.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import br.com.jorchestra.canonical.JOrchestraHandle;
import br.com.jorchestra.canonical.JOrchestraStateCall;
import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
import br.com.jorchestra.dto.JOrchestraSystemEvent;

public class JOrchestraEventWebSocketController extends JOrchestraWebSocketTemplate
		implements MessageListener<JOrchestraSystemEvent> {

	public JOrchestraEventWebSocketController(final JOrchestraHandle jOrchestraHandle,
			final ITopic<JOrchestraStateCall> jOrchestraStateCallTopic,
			final JOrchestraConfigurationProperties jOrchestraConfigurationProperties) {
		super(jOrchestraHandle, jOrchestraStateCallTopic, jOrchestraConfigurationProperties);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraEventWebSocketController.class);

	@Override
	public void onMessage(final Message<JOrchestraSystemEvent> message) {
		super.webSocketSessionMap //
				.entrySet() //
				.parallelStream() //
				.forEach(action -> {
					try {
						sendEnvent(action.getValue(), message.getMessageObject());
					} catch (IOException e) {
						LOGGER.error("m=onMessage, message=" + message, e);
					}
				});
	}

	@Override
	protected void onMessage(final WebSocketSession webSocketSession, final TextMessage textMessage,
			JOrchestraStateCall jOrchestraStateCallWaiting) throws Exception {

	}

	private void sendEnvent(final WebSocketSession webSocketSession, final Object object)
			throws IOException, JsonProcessingException {
		final ObjectMapper objectMapper = new ObjectMapper();
		final String payload = objectMapper.writeValueAsString(object);
		LOGGER.debug("m=sendCallback, payload=" + payload);
		webSocketSession.sendMessage(new TextMessage(payload));
	}
}
