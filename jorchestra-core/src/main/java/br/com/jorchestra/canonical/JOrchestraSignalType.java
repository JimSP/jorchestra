package br.com.jorchestra.canonical;

import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;

public interface JOrchestraSignalType {

	public default void addConfig(final String jorchestraPath, final Config config) {
		
	}

	public default <T> T createService(final String jorchestraPath, final Boolean reliable,
			final HazelcastInstance hazelcastInstance, final Class<T> messageType, final Class<T> classType) {
		return null;
	}

	public Class<?> getMessageType();

	public <T> Class<T> getClassType();

	public default void register(final ITopic<JOrchestraStateCall> jOrchestraStateCallTopic,
			final String jorchestraPath, final JOrchestraHandle jOrchestraHandle,
			final WebSocketHandlerRegistry webSocketHandlerRegistry,
			final JOrchestraConfigurationProperties jOrchestraConfigurationProperties, final Object iService) {
		
	}
}
