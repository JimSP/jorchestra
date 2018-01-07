package org.springframework.context.annotation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.hazelcast.config.Config;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

import br.com.jorchestra.canonical.JOrchestraHandle;
import br.com.jorchestra.canonical.JOrchestraSignal;
import br.com.jorchestra.canonical.JOrchestraStateCall;
import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
import br.com.jorchestra.controller.JOrchestraAdminWebSocket;
import br.com.jorchestra.controller.JOrchestraMonitorWebSocket;
import br.com.jorchestra.service.JOrchestraBeans;
import br.com.jorchestra.util.JOrchestraContextUtils;

@Configuration(value = "jOrchestraAutoConfiguration")
@EnableWebMvc
@EnableWebSocket
@EnableConfigurationProperties(JOrchestraConfigurationProperties.class)
public class JOrchestraAutoConfiguration extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraAutoConfiguration.class);

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private JOrchestraConfigurationProperties jOrchestraConfigurationProperties;

	@Bean
	public JOrchestraBeans JOrchestraBeans() {
		return new JOrchestraBeans();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void registerWebSocketHandlers(final WebSocketHandlerRegistry webSocketHandlerRegistry) {
		LOGGER.info("m=registerWebSocketHandlers");

		JOrchestraContextUtils.setApplicationContext(applicationContext);

		final JOrchestraMonitorWebSocket jOrchestraMonitorWebSocket = new JOrchestraMonitorWebSocket();
		final JOrchestraAdminWebSocket jOrchestraAdminWebSocket = new JOrchestraAdminWebSocket(jOrchestraConfigurationProperties, JOrchestraContextUtils.getExecutorServiceMap());
		
		final Config config = hazelCastConfig(jOrchestraConfigurationProperties.getClusterName());

		final List<JOrchestraHandle> list = JOrchestraContextUtils.jorchestraHandleConsumer( //
				(jOrchestraHandle) -> {
					final JOrchestraSignal jOrchestraSignal = jOrchestraHandle.getjOrchestraSignal();
					registerJOrchestraPath(jOrchestraHandle, config, jOrchestraSignal);
				});
		
		final HazelcastInstance hazelcastInstance = hazelcastInstance(config);
		final ITopic<JOrchestraStateCall> jOrchestraStateCallTopic = hazelcastInstance
				.getReliableTopic("jOrchestraStateCallTopic");
		jOrchestraStateCallTopic.addMessageListener(jOrchestraMonitorWebSocket);

		list.forEach(jOrchestraHandle -> {
			final String jorchestraPath = jOrchestraHandle.getJOrchestraPath();
			final JOrchestraSignal jOrchestraSignal = jOrchestraHandle.getjOrchestraSignal();
			final Boolean reliable = jOrchestraHandle.isReliable();
			final Class<?> messageType = jOrchestraSignal.getMessageType();
			final Class classType = jOrchestraSignal.getClassType();

			final Object iService = jOrchestraSignal.createService(jorchestraPath, reliable, hazelcastInstance,
					messageType, classType);

			jOrchestraSignal.register(jOrchestraStateCallTopic, jorchestraPath, jOrchestraHandle,
					webSocketHandlerRegistry, jOrchestraConfigurationProperties, iService);
		});

		webSocketHandlerRegistry //
				.addHandler(jOrchestraMonitorWebSocket, "jOrchestra-monitor") //
				.setAllowedOrigins(jOrchestraConfigurationProperties.getAllowedOrigins());
		
		webSocketHandlerRegistry //
				.addHandler(jOrchestraAdminWebSocket, "jOrchestra-admin")
				.setAllowedOrigins(jOrchestraConfigurationProperties.getAllowedOrigins());
	}

	@Override
	public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
		LOGGER.info("m=configureDefaultServletHandling");
		configurer.enable();
	}

	private void registerJOrchestraPath(final JOrchestraHandle jOrchestraHandle, final Config config,
			final JOrchestraSignal jOrchestraSignal) {
		final String jorchestraPath = jOrchestraHandle.getJOrchestraPath();
		LOGGER.info("m=registerJOrchestraPath, jorchestraPath=" + jorchestraPath);
		jOrchestraSignal.addConfig(jorchestraPath, config);
	}

	private static Config hazelCastConfig(final String memberName) {
		final Config config = new Config(memberName);
		config.addTopicConfig(new TopicConfig("jOrchestraStateCallTopic"));
		return config;
	}

	private static HazelcastInstance hazelcastInstance(final Config config) {
		return Hazelcast.getOrCreateHazelcastInstance(config);
	}
}
