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
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import br.com.jorchestra.annotation.JOrchestraSignal;
import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
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
		final Config config = hazelCastConfig(jOrchestraConfigurationProperties.getClusterName());

		final List<JOrchestraHandle> list = JOrchestraContextUtils.jorchestraHandleConsumer( //
				(jOrchestraHandle) -> {
					final JOrchestraSignal jOrchestraSignal = jOrchestraHandle.getjOrchestraSignal();

					registerJOrchestraPath(jOrchestraHandle, config, jOrchestraSignal);
				});

		final HazelcastInstance hazelcastInstance = hazelcastInstance(config);

		list.forEach(jOrchestraHandle -> {
			final String jorchestraPath = jOrchestraHandle.getJOrchestraPath();
			final JOrchestraSignal jOrchestraSignal = jOrchestraHandle.getjOrchestraSignal();
			final Boolean reliable = jOrchestraHandle.isReliable();
			final Class<?> messageType = jOrchestraSignal.getMessageType();
			final Class classType = jOrchestraSignal.getClassType();

			final Object iService = jOrchestraSignal.createService(jorchestraPath, reliable, hazelcastInstance,
					messageType, classType);

			jOrchestraSignal.register(jorchestraPath, jOrchestraHandle, webSocketHandlerRegistry,
					jOrchestraConfigurationProperties, iService);
		});
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
		return config;
	}

	private static HazelcastInstance hazelcastInstance(final Config config) {
		return Hazelcast.getOrCreateHazelcastInstance(config);
	}
}
