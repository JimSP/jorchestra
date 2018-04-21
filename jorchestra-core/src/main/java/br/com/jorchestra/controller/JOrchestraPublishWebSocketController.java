package br.com.jorchestra.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ITopic;

import br.com.jorchestra.callable.JOrchestraCallable;
import br.com.jorchestra.canonical.JOrchestraHandle;
import br.com.jorchestra.canonical.JOrchestraStateCall;
import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;

public class JOrchestraPublishWebSocketController extends JOrchestraWebSocketTemplate {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraMessageWebSocketController.class);
	
	private final ObjectMapper objectMapper = new ObjectMapper();

	private final IQueue<JOrchestraCallable> queue;

	public JOrchestraPublishWebSocketController(final JOrchestraHandle jOrchestraHandle,
			final ITopic<JOrchestraStateCall> jOrchestraStateCallTopic,
			final JOrchestraConfigurationProperties jOrchestraConfigurationProperties,
			final IQueue<JOrchestraCallable> queue) {
		super(jOrchestraHandle, jOrchestraStateCallTopic, jOrchestraConfigurationProperties);
		this.queue = queue;
	}

	@Override
	public void afterConnectionEstablished(final WebSocketSession webSocketSession) throws Exception {
		super.afterConnectionEstablished(webSocketSession);
	}

	@Override
	public void afterConnectionClosed(final WebSocketSession webSocketSession, final CloseStatus closeStatus)
			throws Exception {
		super.afterConnectionClosed(webSocketSession, closeStatus);
	}

	@Override
	public void handleTextMessage(final WebSocketSession webSocketSession, final TextMessage textMessage)
			throws Exception {
		try {
			super.handleTextMessage(webSocketSession, textMessage);
		} catch (Throwable t) {
			LOGGER.debug("m=handleTextMessage, sessionId=, " + webSocketSession.getId() + ", payload="
					+ textMessage.getPayload(), t);

			final JOrchestraStateCall jOrchestraStateCall_Error = JOrchestraStateCall.createJOrchestraStateCall_ERROR(
					jOrchestraConfigurationProperties.getClusterName(), jOrchestraConfigurationProperties.getName(),
					webSocketSession.getUri().getPath(), webSocketSession.getId(), System.currentTimeMillis(),
					System.currentTimeMillis(), textMessage.getPayload());

			super.jOrchestraStateCallTopic.publish(jOrchestraStateCall_Error);

			throw new Exception("m=handleTextMessage, sessionId=, " + webSocketSession.getId() + ", payload="
					+ textMessage.getPayload(), t);
		}
	}

	@Override
	public void handleTransportError(final WebSocketSession webSocketSession, final Throwable e) throws Exception {
		final JOrchestraStateCall jOrchestraStateCall_Error = JOrchestraStateCall.createJOrchestraStateCall_ERROR(
				jOrchestraConfigurationProperties.getClusterName(), jOrchestraConfigurationProperties.getName(),
				webSocketSession.getUri().getPath(), webSocketSession.getId(), System.currentTimeMillis(),
				System.currentTimeMillis(), null);

		super.jOrchestraStateCallTopic.publish(jOrchestraStateCall_Error);

		super.handleTransportError(webSocketSession, e);
	}

	@Override
	protected void onMessage(final WebSocketSession webSocketSession, final TextMessage textMessage,
			final JOrchestraStateCall jOrchestraStateCallWaiting)
			throws JsonParseException, JsonMappingException, IllegalAccessException, InvocationTargetException,
			JsonProcessingException, IOException, InterruptedException, ExecutionException {
		invokeJOrchestraBean(webSocketSession, textMessage, jOrchestraStateCallWaiting);
	}

	private void invokeJOrchestraBean(final WebSocketSession webSocketSession, final TextMessage textMessage,
			final JOrchestraStateCall jOrchestraStateCallWaiting) throws JsonProcessingException, IOException {

		final String payload = textMessage.getPayload();
		final JOrchestraCallable jOrchestraCallable = createJOrchestraCallable(payload);

		queue.add(jOrchestraCallable);

		final JOrchestraStateCall jOrchestraStateCall_Processing = JOrchestraStateCall
				.createJOrchestraStateCall_PROCESSING(jOrchestraStateCallWaiting, payload);

		super.jOrchestraStateCallTopic.publish(jOrchestraStateCall_Processing);
	}

	private JOrchestraCallable createJOrchestraCallable(final String payload)
			throws JsonParseException, JsonMappingException, IOException {
		final Class<?>[] parameterTypes = jOrchestraHandle.getJorchestraParametersType();

		final List<Object> list = createParametersTtype(payload, parameterTypes);

		final JOrchestraCallable jOrchestraCallable = new JOrchestraCallable(jOrchestraHandle, parameterTypes,
				list.toArray());

		return jOrchestraCallable;
	}

	private List<Object> createParametersTtype(final String payload,
			final Class<?>[] parameterTypes) throws IOException, JsonParseException, JsonMappingException {
		final List<Object> list = new ArrayList<>();
		for (Class<?> parameterClass : parameterTypes) {
			final Object parameter = objectMapper.readValue(payload, parameterClass);
			list.add(parameter);
		}
		return list;
	}
}
