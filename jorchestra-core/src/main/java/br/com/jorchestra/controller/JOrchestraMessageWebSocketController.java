package br.com.jorchestra.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	private final Map<String, Map<JOrchestraStateCall, Future<Object>>> executorServiceMap;

	public JOrchestraMessageWebSocketController(final JOrchestraHandle jOrchestraHandle,
			final ITopic<JOrchestraStateCall> jOrchestraStateCallTopic,
			final JOrchestraConfigurationProperties jOrchestraConfigurationProperties,
			final IExecutorService executorService,
			final Map<String, Map<JOrchestraStateCall, Future<Object>>> executorServiceMap) {
		super(jOrchestraHandle, jOrchestraStateCallTopic, jOrchestraConfigurationProperties);
		this.executorService = executorService;
		this.executorServiceMap = executorServiceMap;
	}

	@Override
	public void afterConnectionEstablished(final WebSocketSession webSocketSession) throws Exception {
		super.afterConnectionEstablished(webSocketSession);
		executorServiceMap.put(webSocketSession.getId(), Collections.synchronizedMap(new HashMap<>()));
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

	@Override
	public void handleTransportError(final WebSocketSession webSocketSession, final Throwable e) throws Exception {
		final String sessionId = webSocketSession.getId();
		executorServiceMap.get(sessionId).clear();
		super.handleTransportError(webSocketSession, e);
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

		final Map<JOrchestraStateCall, Future<Object>> joOrchestraStateCalls = executorServiceMap
				.get(webSocketSession.getId());
		joOrchestraStateCalls.put(jOrchestraStateCall_Processing, future);

		super.jOrchestraStateCallTopic.publish(jOrchestraStateCall_Processing);

		try {
			final Object result = future.get();
			sendCallback(webSocketSession, result);

			if (future.isCancelled()) {
				final JOrchestraStateCall jOrchestraStateCall_Success = JOrchestraStateCall
						.createJOrchestraStateCall_SUCCESS(jOrchestraStateCall_Processing, payload);
				super.jOrchestraStateCallTopic.publish(jOrchestraStateCall_Success);
				joOrchestraStateCalls.remove(jOrchestraStateCall_Success);
			} else {
				final JOrchestraStateCall jOrchestraStateCall_Canceled = JOrchestraStateCall
						.createJOrchestraStateCall_CANCELED(jOrchestraStateCall_Processing, payload);
				super.jOrchestraStateCallTopic.publish(jOrchestraStateCall_Canceled);
				joOrchestraStateCalls.remove(jOrchestraStateCall_Canceled);
			}
		} catch (InterruptedException | ExecutionException e) {

			LOGGER.error(
					"handleTextMessage, jOrchestraStateCall=" + jOrchestraStateCall_Processing + ", payload=" + payload,
					e);

			final JOrchestraStateCall jOrchestraStateCall_Error = JOrchestraStateCall
					.createJOrchestraStateCall_ERROR(jOrchestraStateCall_Processing, payload);
			super.jOrchestraStateCallTopic.publish(jOrchestraStateCall_Error);
			joOrchestraStateCalls.remove(jOrchestraStateCall_Error);
		} finally {
			executorServiceMap.remove(webSocketSession.getId());
		}
	}

	private void sendCallback(final WebSocketSession webSocketSession, final Object object)
			throws IOException, JsonProcessingException {
		final ObjectMapper objectMapper = new ObjectMapper();
		final String payload = objectMapper.writeValueAsString(object);
		LOGGER.debug("m=sendCallback, payload=" + payload);
		webSocketSession.sendMessage(new TextMessage(payload));
	}
}
