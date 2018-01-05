package org.springframework.context.annotation;

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
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;

import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
import br.com.jorchestra.controller.JOrchestraWebSocketController;
import br.com.jorchestra.handle.JOrchestraHandle;
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
	private JOrchestraConfigurationProperties jorchestraConfigurationProperties;

	@Bean
	public JOrchestraBeans JOrchestraBeans() {
		return new JOrchestraBeans();
	}

	@Override
	public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
		LOGGER.info("m=registerWebSocketHandlers");
		JOrchestraContextUtils.setApplicationContext(applicationContext);
		JOrchestraContextUtils.jorchestraHandleConsumer( //
				(jOrchestraHandle) -> registerJOrchestraPath(registry, jOrchestraHandle));
	}

	@Override
	public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
		LOGGER.info("m=configureDefaultServletHandling");
		configurer.enable();
	}

	private HazelcastInstance hazelcastInstance(final Config config) {
		LOGGER.info("m=hazelcastInstance, config=" + config);
		return Hazelcast.getOrCreateHazelcastInstance(config);
	}

	private Config hazelCastConfig(final String memberName) {
		final Config config = new Config(memberName);
		return config;
	}

	private void registerJOrchestraPath(final WebSocketHandlerRegistry registry,
			final JOrchestraHandle jOrchestraHandle) {

		final String jorchestraPath = jOrchestraHandle.getJOrchestraPath();

		LOGGER.info("m=registerJOrchestraPath, jorchestraPath=" + jorchestraPath);

		final Config config = hazelCastConfig(jorchestraConfigurationProperties.getClusterName());

		config.addExecutorConfig(createExecutorConfig(jorchestraPath));

		final HazelcastInstance hazelcastInstance = hazelcastInstance(config);

		final IExecutorService executorService = createExecutorService(jorchestraPath, hazelcastInstance);

		final JOrchestraWebSocketController jOrchestraWebSocketController = jOrchestraWebSocketController(
				jOrchestraHandle, executorService);

		registry.addHandler(jOrchestraWebSocketController, jorchestraPath) //
				.setAllowedOrigins(jorchestraConfigurationProperties.getAllowedOrigins());
	}

	private ExecutorConfig createExecutorConfig(final String jorchestraPath) {
		return new ExecutorConfig(jorchestraPath);
	}

	private <T> IExecutorService createExecutorService(final String jorchestraPath,
			final HazelcastInstance hazelcastInstance) {
		return hazelcastInstance.getExecutorService(jorchestraPath);
	}

	private JOrchestraWebSocketController jOrchestraWebSocketController(final JOrchestraHandle jOrchestraHandle,
			final IExecutorService executorService) {
		return new JOrchestraWebSocketController(jOrchestraHandle, executorService);
	}
}
