package br.com.jorchestra.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.ITopic;

import br.com.jorchestra.callable.JOrchestraCallable;
import br.com.jorchestra.callable.JOrchestraCallableFailOver;
import br.com.jorchestra.canonical.JOrchestraHandle;
import br.com.jorchestra.canonical.JOrchestraStateCall;
import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;

public class JOrchestraMessageWebSocketController extends JOrchestraWebSocketTemplate {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraMessageWebSocketController.class);
	
	private final ObjectMapper objectMapper = new ObjectMapper();

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
	public void afterConnectionClosed(final WebSocketSession webSocketSession, final CloseStatus closeStatus)
			throws Exception {
		executorServiceMap.get(webSocketSession.getId()).clear();
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

		final Future<Object> future = executorService.submit(jOrchestraCallable);

		final JOrchestraStateCall jOrchestraStateCall_Processing = JOrchestraStateCall
				.createJOrchestraStateCall_PROCESSING(jOrchestraStateCallWaiting, payload);

		final Map<JOrchestraStateCall, Future<Object>> joOrchestraStateCalls = executorServiceMap
				.get(webSocketSession.getId());
		joOrchestraStateCalls.put(jOrchestraStateCall_Processing, future);

		super.jOrchestraStateCallTopic.publish(jOrchestraStateCall_Processing);

		try {
			final Object result = future.get();
			sendCallback(webSocketSession, result);

			verifyState(payload, future, jOrchestraStateCall_Processing, joOrchestraStateCalls);
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error(
					"handleTextMessage, jOrchestraStateCall=" + jOrchestraStateCall_Processing + ", payload=" + payload,
					e);

			proccessError(payload, jOrchestraStateCall_Processing, joOrchestraStateCalls);

			if (jOrchestraHandle.getFailOverMethodName() != null) {
				final JOrchestraCallableFailOver jOrchestraCallableFailOver = createJOrchestraCallableFailOver(payload);

				final Future<Object> futureFailOver = executorService.submit(jOrchestraCallableFailOver);

				final JOrchestraStateCall jOrchestraStateCall_ProcessingFailOver = JOrchestraStateCall
						.createJOrchestraStateCall_PROCESSING_FAILOVER(jOrchestraStateCall_Processing, payload);
				joOrchestraStateCalls.put(jOrchestraStateCall_ProcessingFailOver, futureFailOver);

				super.jOrchestraStateCallTopic.publish(jOrchestraStateCall_ProcessingFailOver);

				try {
					final Object result = futureFailOver.get();
					sendCallback(webSocketSession, result);

					verifyState(payload, futureFailOver, jOrchestraStateCall_ProcessingFailOver, joOrchestraStateCalls);
				} catch (InterruptedException | ExecutionException e1) {
					LOGGER.error(
							"handleTextMessage, jOrchestraStateCall=" + jOrchestraStateCall_ProcessingFailOver + ", payload=" + payload,
							e);
					
					proccessError(payload, jOrchestraStateCall_ProcessingFailOver, joOrchestraStateCalls);
				}
			}

		} catch (CancellationException e) {
			final JOrchestraStateCall jOrchestraStateCall_Canceled = JOrchestraStateCall
					.createJOrchestraStateCall_CANCELED(jOrchestraStateCall_Processing, payload);
			super.jOrchestraStateCallTopic.publish(jOrchestraStateCall_Canceled);
			joOrchestraStateCalls.remove(jOrchestraStateCall_Canceled);
		}
	}

	private void proccessError(final String payload, final JOrchestraStateCall jOrchestraStateCall_Processing,
			final Map<JOrchestraStateCall, Future<Object>> joOrchestraStateCalls) {
		final JOrchestraStateCall jOrchestraStateCall_Error = JOrchestraStateCall
				.createJOrchestraStateCall_ERROR(jOrchestraStateCall_Processing, payload);
		super.jOrchestraStateCallTopic.publish(jOrchestraStateCall_Error);
		joOrchestraStateCalls.remove(jOrchestraStateCall_Error);
	}

	private JOrchestraCallableFailOver createJOrchestraCallableFailOver(final String payload)
			throws JsonParseException, JsonMappingException, IOException {
		final Class<?>[] parameterTypes = jOrchestraHandle.getJorchestraParametersType();

		final List<Object> list = createParametersTtype(payload, parameterTypes);

		final JOrchestraCallableFailOver jOrchestraCallable = new JOrchestraCallableFailOver(jOrchestraHandle,
				parameterTypes, list.toArray());

		return jOrchestraCallable;
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

	private void verifyState(final String payload, final Future<Object> future,
			final JOrchestraStateCall jOrchestraStateCall_Processing,
			final Map<JOrchestraStateCall, Future<Object>> joOrchestraStateCalls) {
		if (future.isCancelled()) {
			final JOrchestraStateCall jOrchestraStateCall_Canceled = JOrchestraStateCall
					.createJOrchestraStateCall_CANCELED(jOrchestraStateCall_Processing, payload);
			super.jOrchestraStateCallTopic.publish(jOrchestraStateCall_Canceled);
			joOrchestraStateCalls.remove(jOrchestraStateCall_Canceled);
		} else {
			final JOrchestraStateCall jOrchestraStateCall_Success = JOrchestraStateCall
					.createJOrchestraStateCall_SUCCESS(jOrchestraStateCall_Processing, payload);
			super.jOrchestraStateCallTopic.publish(jOrchestraStateCall_Success);
			joOrchestraStateCalls.remove(jOrchestraStateCall_Success);
		}
	}

	private void sendCallback(final WebSocketSession webSocketSession, final Object object)
			throws IOException, JsonProcessingException {
		final String payload = objectMapper.writeValueAsString(object);
		LOGGER.debug("m=sendCallback, payload=" + payload);
		webSocketSession.sendMessage(new TextMessage(payload));
	}
}
