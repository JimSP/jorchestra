package br.com.jorchestra.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.ITopic;

import br.com.jorchestra.callable.JOrchestraCallable;
import br.com.jorchestra.canonical.JOrchestraHandle;
import br.com.jorchestra.canonical.JOrchestraStateCall;
import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;

public class JOrchestraMessageWebSocketController extends JOrchestraWebSocketTemplate {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraMessageWebSocketController.class);

	private final IExecutorService executorService;

	public JOrchestraMessageWebSocketController(final JOrchestraHandle jOrchestraHandle,
			final ITopic<JOrchestraStateCall> jOrchestraStateCallTopic,
			final JOrchestraConfigurationProperties jOrchestraConfigurationProperties,
			final IExecutorService executorService) {
		super(jOrchestraHandle, jOrchestraStateCallTopic, jOrchestraConfigurationProperties);
		this.executorService = executorService;
	}

	@Override
	public void handleTextMessage(final WebSocketSession webSocketSession, final TextMessage textMessage)
			throws Exception {
		try {
			super.handleTextMessage(webSocketSession, textMessage);
			invokeJOrchestraBean(webSocketSession, textMessage.getPayload());
		} catch (Throwable t) {
			LOGGER.debug("handleTextMessage, " + webSocketSession.getId() + ", payload=" + textMessage.getPayload(), t);
			throw new Exception(
					"handleTextMessage, " + webSocketSession.getId() + ", payload=" + textMessage.getPayload(), t);
		}
	}

	private void invokeJOrchestraBean(final WebSocketSession webSocketSession, final String payload)
			throws IllegalAccessException, InvocationTargetException, IOException, JsonParseException,
			JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException {

		final ObjectMapper objectMapper = new ObjectMapper();
		final Class<?>[] parameterTypes = jOrchestraHandle.getJorchestraParametersType();

		final List<Object> list = new ArrayList<>();
		for (Class<?> parameterClass : parameterTypes) {
			final Object parameter = objectMapper.readValue(payload, parameterClass);
			list.add(parameter);
		}

		final JOrchestraCallable JOrchestraCallable = new JOrchestraCallable(jOrchestraHandle, parameterTypes,
				list.toArray());

		final Future<Object> future = executorService.submit(JOrchestraCallable);

		final JOrchestraStateCall jOrchestraStateCall_Processing = JOrchestraStateCall
				.createJOrchestraStateCall_PROCESSING(webSocketSession.getId(),
						jOrchestraConfigurationProperties.getClusterName(), jOrchestraConfigurationProperties.getName(),
						payload);

		super.jOrchestraStateCallTopic.publish(jOrchestraStateCall_Processing);

		final Object result = future.get();
		sendCallback(webSocketSession, result);

		final JOrchestraStateCall jOrchestraStateCall_Success = JOrchestraStateCall.createJOrchestraStateCall_SUCCESS(
				webSocketSession.getId(), jOrchestraConfigurationProperties.getClusterName(),
				jOrchestraConfigurationProperties.getName(), payload);

		super.jOrchestraStateCallTopic.publish(jOrchestraStateCall_Success);
	}

	private void sendCallback(final WebSocketSession webSocketSession, final Object object)
			throws IOException, JsonProcessingException {
		final ObjectMapper objectMapper = new ObjectMapper();
		final String payload = objectMapper.writeValueAsString(object);
		LOGGER.debug("m=sendCallback, payload=" + payload);
		webSocketSession.sendMessage(new TextMessage(payload));
	}
}
