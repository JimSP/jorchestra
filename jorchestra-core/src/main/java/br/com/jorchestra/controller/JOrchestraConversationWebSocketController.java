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

public class JOrchestraConversationWebSocketController extends JOrchestraWebSocketTemplate
		implements MessageListener<String[]> {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraConversationWebSocketController.class);

	private final ITopic<String[]> topic;

	public JOrchestraConversationWebSocketController(final JOrchestraHandle jOrchestraHandle,
			final ITopic<JOrchestraStateCall> jOrchestraStateCallTopic,
			final JOrchestraConfigurationProperties jOrchestraConfigurationProperties, final ITopic<String[]> topic) {
		super(jOrchestraHandle, jOrchestraStateCallTopic, jOrchestraConfigurationProperties);
		this.topic = topic;
	}

	@Override
	public void handleTextMessage(final WebSocketSession webSocketSession, final TextMessage textMessage)
			throws Exception {
		super.handleTextMessage(webSocketSession, textMessage);
	}

	@Override
	public void onMessage(final Message<String[]> message) {
		super.webSocketSessionMap //
				.entrySet() //
				.parallelStream() //
				.forEach(action -> {
					try {
						final String[] payloadId = message.getMessageObject();
						sendEnvent(action.getValue(), payloadId);
					} catch (IOException e) {
						LOGGER.error("m=onMessage, message=" + message, e);
					}
				});
	}

	@Override
	protected void onMessage(final WebSocketSession webSocketSession, final TextMessage textMessage,
			final JOrchestraStateCall jOrchestraStateCallWaiting) throws Exception {
		try {
			topic.publish(new String[] { textMessage.getPayload(), webSocketSession.getId() });
		} catch (Throwable t) {
			LOGGER.debug("m=handleTextMessage, sessionId=, " + webSocketSession.getId() + ", payload="
					+ textMessage.getPayload(), t);
			throw new Exception("m=handleTextMessage, sessionId=, " + webSocketSession.getId() + ", payload="
					+ textMessage.getPayload(), t);
		}
	}

	private void sendEnvent(final WebSocketSession webSocketSession, final String[] messageId)
			throws IOException, JsonProcessingException {

		if (!webSocketSession.getId().equals(messageId[1])) {
			final ObjectMapper objectMapper = new ObjectMapper();
			final String payload = objectMapper.writeValueAsString(messageId[0]);
			LOGGER.debug("m=sendEnvent, payload=" + payload);
			webSocketSession.sendMessage(new TextMessage(payload));
		}
	}
}
