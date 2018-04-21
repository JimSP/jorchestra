package br.com.jorchestra.canonical;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.hazelcast.config.Config;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ITopic;

import br.com.jorchestra.callable.JOrchestraCallable;
import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
import br.com.jorchestra.controller.JOrchestraEventWebSocketController;
import br.com.jorchestra.controller.JOrchestraMessageWebSocketController;
import br.com.jorchestra.controller.JOrchestraNotificationWebSocketController;
import br.com.jorchestra.controller.JOrchestraPublishWebSocketController;
import br.com.jorchestra.dto.JOrchestraNotification;
import br.com.jorchestra.dto.JOrchestraPublishData;
import br.com.jorchestra.dto.JOrchestraSystemEvent;
import br.com.jorchestra.util.JOrchestraContextUtils;

public enum JOrchestraSignal implements JOrchestraSignalType {

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
					(IExecutorService) iService, JOrchestraContextUtils.getExecutorServiceMap());

			webSocketHandlerRegistry.addHandler(jOrchestraWebSocketController, jorchestraPath) //
					.setAllowedOrigins(jOrchestraConfigurationProperties.getAllowedOrigins());

		}
	},

	PUBLISH {
		@Override
		public void addConfig(final String jorchestraPath, final Config config) {
			config.addQueueConfig(createQueueConfig(jorchestraPath));
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T createService(final String jorchestraPath, final Boolean reliable,
				final HazelcastInstance hazelcastInstance, final Class<T> messageType, final Class<T> classType) {
			return (T) createQueue(jorchestraPath, hazelcastInstance, messageType);
		}

		@Override
		public Class<?> getMessageType() {
			return JOrchestraPublishData.class;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> Class<T> getClassType() {
			return (Class<T>) IQueue.class;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void register(final ITopic<JOrchestraStateCall> jOrchestraStateCallTopic, final String jorchestraPath,
				final JOrchestraHandle jOrchestraHandle, final WebSocketHandlerRegistry webSocketHandlerRegistry,
				final JOrchestraConfigurationProperties jOrchestraConfigurationProperties, final Object iService) {

			final IQueue<JOrchestraCallable> queue = (IQueue<JOrchestraCallable>) iService;

			Executors.newFixedThreadPool(jOrchestraConfigurationProperties.getPoolSize())
					.submit(() -> pooling(queue, jOrchestraConfigurationProperties));

			final JOrchestraPublishWebSocketController jOrchestraWebSocketController = new JOrchestraPublishWebSocketController(
					jOrchestraHandle, jOrchestraStateCallTopic, jOrchestraConfigurationProperties, queue);

			webSocketHandlerRegistry.addHandler(jOrchestraWebSocketController, jorchestraPath) //
					.setAllowedOrigins(jOrchestraConfigurationProperties.getAllowedOrigins());
		}

		private void pooling(final IQueue<JOrchestraCallable> queue,
				final JOrchestraConfigurationProperties jOrchestraConfigurationProperties) {
			while (true) {
				try {
					final JOrchestraCallable jOrchestraCallable = queue.take();
					try {
						jOrchestraCallable.execute();
						TimeUnit.MILLISECONDS.sleep(jOrchestraConfigurationProperties.getPoolingMilliseconds());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	},

	NOTIFICATION {
		@Override
		public void addConfig(final String jorchestraPath, final Config config) {
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
		public void addConfig(final String jorchestraPath, final Config config) {
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

	protected static ExecutorConfig createExecutorConfig(final String jorchestraPath) {
		return new ExecutorConfig(jorchestraPath);
	}

	protected static QueueConfig createQueueConfig(final String jorchestraPath) {
		return new QueueConfig(jorchestraPath);
	}

	protected static TopicConfig createTopicConfig(final String jorchestraPath) {
		return new TopicConfig(jorchestraPath);
	}

	protected static IExecutorService createExecutorService(final String jorchestraPath,
			final HazelcastInstance hazelcastInstance) {
		return hazelcastInstance.getExecutorService(jorchestraPath);
	}

	private static <T> IQueue<T> createQueue(final String jorchestraPath,
			final HazelcastInstance hazelcastInstance, final Class<T> clazz) {
		return hazelcastInstance.getQueue(jorchestraPath);
	}

	protected static <T> ITopic<T> createTopic(final String jorchestraPath, final Boolean reliable,
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
		final HandshakeInterceptor[] interceptors = null;

		webSocketHandlerRegistry.addHandler(webSocketHandler, jorchestraPath) //
				.setAllowedOrigins(jOrchestraConfigurationProperties.getAllowedOrigins()).addInterceptors(interceptors);
	}
}
