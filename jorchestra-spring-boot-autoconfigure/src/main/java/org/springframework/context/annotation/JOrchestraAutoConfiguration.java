package org.springframework.context.annotation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.hazelcast.config.Config;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;

import br.com.jorchestra.annotation.JOrchestra;
import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
import br.com.jorchestra.controller.JOrchestraWebSocketController;
import br.com.jorchestra.listener.JOrchestraEventListener;
import br.com.jorchestra.listener.JOrchestraEventListenerCallback;

@Configuration
@EnableWebMvc
@EnableWebSocket
@EnableConfigurationProperties(JOrchestraConfigurationProperties.class)
public class JOrchestraAutoConfiguration extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraAutoConfiguration.class);

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private JOrchestraConfigurationProperties jorchestraConfigurationProperties;

	public HazelcastInstance hazelcastInstance(final Config config) {
		LOGGER.info("m=hazelcastInstance, config=" + config);
		return Hazelcast.getOrCreateHazelcastInstance(config);
	}

	@Override
	public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
		LOGGER.info("m=registerWebSocketHandlers");

		loadJOrchestraBeans().parallelStream().forEach(jorchestraBean -> {

			final JOrchestra jOrchestra = jorchestraBean.getClass().getAnnotation(JOrchestra.class);

			Arrays.asList(jorchestraBean.getClass().getDeclaredMethods()).parallelStream()
					.filter(method -> method.getModifiers() == Modifier.PUBLIC).forEach(method -> {
						final String path = jOrchestra.path();
						final String methodName = method.getName();

						registerJOrchestraPath(registry, jorchestraBean, method, path, methodName);
					});
		});
	}

	@Override
	public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
		LOGGER.info("m=configureDefaultServletHandling");

		configurer.enable();
	}

	private Config hazelCastConfig(final String memberName) {
		final Config config = new Config(memberName);
		return config;
	}

	private void registerJOrchestraPath(final WebSocketHandlerRegistry registry, final Object jorchestraBean,
			final Method method, final String path, final String methodName) {

		final String jorchestraPath = "/" + path + "-" + methodName;

		LOGGER.info("m=registerJOrchestraPath, jorchestraPath=" + jorchestraPath);

		final JOrchestraEventListenerCallback jOrchestraEventListenerCallback = new JOrchestraEventListenerCallback();

		final Config config = hazelCastConfig(jorchestraConfigurationProperties.getInstanceName());

		configTopicResult(jorchestraPath + "-callback", jOrchestraEventListenerCallback, config);

		final HazelcastInstance hazelcastInstance = hazelcastInstance(config);

		final ITopic<Object> resultTopic = createTopic(jorchestraPath + "callback", jOrchestraEventListenerCallback,
				hazelcastInstance);

		final ITopic<Object[]> topic = createTopic(jorchestraPath,
				new JOrchestraEventListener(jorchestraBean, method, resultTopic), hazelcastInstance);

		final JOrchestraWebSocketController jOrchestraWebSocketController = jOrchestraWebSocketController(path, method,
				topic);

		jOrchestraEventListenerCallback.setJOrchestraWebSocketController(jOrchestraWebSocketController);

		registry.addHandler(jOrchestraWebSocketController, jorchestraPath) //
				.setAllowedOrigins(jorchestraConfigurationProperties.getAllowedOrigins());
	}

	private <T> ITopic<T> createTopic(final String jorchestraPath, final MessageListener<T> messageListener,
			final HazelcastInstance hazelcastInstance) {

		final ITopic<T> topic = hazelcastInstance.getTopic(jorchestraPath);
		topic.addMessageListener(messageListener);
		return topic;
	}

	private List<Object> loadJOrchestraBeans() {
		LOGGER.info("m=loadJOrchestraBeans, applicationContext.beanDefinitionNames="
				+ Arrays.toString(applicationContext.getBeanDefinitionNames()));

		final String[] jorcherstraBeans = applicationContext.getBeanNamesForAnnotation(JOrchestra.class);

		LOGGER.info("m=loadJOrchestraBeans, jorcherstraBeans=" + Arrays.toString(jorcherstraBeans));

		return Arrays.asList(jorcherstraBeans) //
				.parallelStream() //
				.map(jorcherstraBeanName -> applicationContext.getBean(jorcherstraBeanName)) //
				.collect(Collectors.toList());
	}

	private JOrchestraWebSocketController jOrchestraWebSocketController(final String path, final Method method,
			final ITopic<Object[]> topic) {
		return new JOrchestraWebSocketController(path, method, topic);
	}

	private <T> void configTopicResult(final String jorchestraPath, final MessageListener<T> messageListener,
			final Config config) {
		final TopicConfig topicConfig = topicConfigResult(jorchestraPath, messageListener);

		config.addTopicConfig(topicConfig);
	}

	private <T> TopicConfig topicConfigResult(final String jorchestraPath, final MessageListener<T> messageListener) {
		return new TopicConfig(jorchestraPath) //
				.setMultiThreadingEnabled(true) //
				.addMessageListenerConfig(listenerConfig(messageListener)); //
	}

	private <T> ListenerConfig listenerConfig(final MessageListener<T> messageListener) {
		return new ListenerConfig(messageListener);
	}
}
