package br.com.jorchestra.canonical;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.hazelcast.config.Config;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.ITopic;

import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
import br.com.jorchestra.controller.JOrchestraEventWebSocketController;
import br.com.jorchestra.controller.JOrchestraMessageWebSocketController;
import br.com.jorchestra.controller.JOrchestraNotificationWebSocketController;
import br.com.jorchestra.dto.JOrchestraNotification;
import br.com.jorchestra.dto.JOrchestraSystemEvent;

public enum JOrchestraSignal {

	MESSAGE {
		@Override
		public void addConfig(final String jorchestraPath, final Config config) {
			config.addExecutorConfig(createExecutorConfig(jorchestraPath));
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T createService(final String jorchestraPath, final Boolean reliable,
				final HazelcastInstance hazelcastInstance, final Class<T> messageType, final Class<T> classType) {
			return (T) createExecutorService(jorchestraPath, hazelcastInstance);
		}

		@Override
		public Class<?> getMessageType() {
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> Class<T> getClassType() {
			return (Class<T>) IExecutorService.class;
		}

		@Override
		public void register(final ITopic<JOrchestraStateCall> jOrchestraStateCallTopic, final String jorchestraPath,
				final JOrchestraHandle jOrchestraHandle, final WebSocketHandlerRegistry webSocketHandlerRegistry,
				final JOrchestraConfigurationProperties jOrchestraConfigurationProperties, final Object iService) {

			final JOrchestraMessageWebSocketController jOrchestraWebSocketController = new JOrchestraMessageWebSocketController(
					jOrchestraHandle, jOrchestraStateCallTopic, jOrchestraConfigurationProperties,
					(IExecutorService) iService);

			webSocketHandlerRegistry.addHandler(jOrchestraWebSocketController, jorchestraPath) //
					.setAllowedOrigins(jOrchestraConfigurationProperties.getAllowedOrigins());

		}
	},
	NOTIFICATION {
		@Override
		public void addConfig(String jorchestraPath, final Config config) {
			config.addTopicConfig(createTopicConfig(jorchestraPath));
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T createService(final String jorchestraPath, final Boolean reliable,
				final HazelcastInstance hazelcastInstance, final Class<T> messageType, final Class<T> classType) {
			return (T) createTopic(jorchestraPath, reliable, hazelcastInstance, messageType);

		}

		@Override
		public Class<?> getMessageType() {
			return JOrchestraNotification.class;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> Class<T> getClassType() {
			return (Class<T>) ITopic.class;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void register(final ITopic<JOrchestraStateCall> jOrchestraStateCallTopic, final String jorchestraPath,
				final JOrchestraHandle jOrchestraHandle, final WebSocketHandlerRegistry webSocketHandlerRegistry,
				final JOrchestraConfigurationProperties jOrchestraConfigurationProperties, final Object iService) {

			final JOrchestraNotificationWebSocketController jOrchestraNotificationWebSocketController = new JOrchestraNotificationWebSocketController(
					jOrchestraHandle, jOrchestraStateCallTopic, jOrchestraConfigurationProperties,
					(ITopic<JOrchestraNotification>) iService);

			((ITopic<JOrchestraNotification>) iService).addMessageListener(jOrchestraNotificationWebSocketController);

			addHandle(jorchestraPath, webSocketHandlerRegistry, jOrchestraConfigurationProperties,
					jOrchestraNotificationWebSocketController);
		}
	},
	EVENT {
		@Override
		public void addConfig(String jorchestraPath, final Config config) {
			config.addTopicConfig(createTopicConfig(jorchestraPath));
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T createService(final String jorchestraPath, final Boolean reliable,
				final HazelcastInstance hazelcastInstance, final Class<T> messageType, final Class<T> classType) {
			return (T) createTopic(jorchestraPath, reliable, hazelcastInstance, messageType);
		}

		@Override
		public Class<?> getMessageType() {
			return JOrchestraSystemEvent.class;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> Class<T> getClassType() {
			return (Class<T>) ITopic.class;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void register(final ITopic<JOrchestraStateCall> jOrchestraStateCallTopic, final String jorchestraPath,
				final JOrchestraHandle jOrchestraHandle, final WebSocketHandlerRegistry webSocketHandlerRegistry,
				final JOrchestraConfigurationProperties jOrchestraConfigurationProperties, final Object iService) {

			final JOrchestraEventWebSocketController jOrchestraEventWebSocketController = new JOrchestraEventWebSocketController(
					jOrchestraHandle, jOrchestraStateCallTopic, jOrchestraConfigurationProperties);

			((ITopic<JOrchestraSystemEvent>) iService).addMessageListener(jOrchestraEventWebSocketController);

			addHandle(jorchestraPath, webSocketHandlerRegistry, jOrchestraConfigurationProperties,
					jOrchestraEventWebSocketController);
		}
	};

	public abstract void addConfig(final String jorchestraPath, final Config config);

	public abstract <T> T createService(final String jorchestraPath, final Boolean reliable,
			final HazelcastInstance hazelcastInstance, final Class<T> messageType, final Class<T> classType);

	public abstract Class<?> getMessageType();

	public abstract <T> Class<T> getClassType();

	public abstract void register(final ITopic<JOrchestraStateCall> jOrchestraStateCallTopic,
			final String jorchestraPath, final JOrchestraHandle jOrchestraHandle,
			final WebSocketHandlerRegistry webSocketHandlerRegistry,
			final JOrchestraConfigurationProperties jOrchestraConfigurationProperties, final Object iService);

	protected static ExecutorConfig createExecutorConfig(final String jorchestraPath) {
		return new ExecutorConfig(jorchestraPath);
	}

	protected static TopicConfig createTopicConfig(final String jorchestraPath) {
		return new TopicConfig(jorchestraPath);
	}

	protected static IExecutorService createExecutorService(final String jorchestraPath,
			final HazelcastInstance hazelcastInstance) {
		return hazelcastInstance.getExecutorService(jorchestraPath);
	}

	protected <T> ITopic<T> createTopic(final String jorchestraPath, final Boolean reliable,
			final HazelcastInstance hazelcastInstance, final Class<T> clazz) {
		if (reliable) {
			return hazelcastInstance.getReliableTopic(jorchestraPath);
		} else {
			return hazelcastInstance.getTopic(jorchestraPath);
		}
	}

	protected void addHandle(final String jorchestraPath, final WebSocketHandlerRegistry webSocketHandlerRegistry,
			final JOrchestraConfigurationProperties jOrchestraConfigurationProperties,
			final WebSocketHandler webSocketHandler) {
		webSocketHandlerRegistry.addHandler(webSocketHandler, jorchestraPath) //
				.setAllowedOrigins(jOrchestraConfigurationProperties.getAllowedOrigins());
	}
}
