package org.springframework.context.annotation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

import br.com.jorchestra.annotation.JOrchestra;
import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
import br.com.jorchestra.controller.JOrchestraWebSocketController;
import br.com.jorchestra.util.JOrchestraContextUtils;

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

		JOrchestraContextUtils.setApplicationContext(applicationContext);

		final Map<String, Object> map = loadJOrchestraBeans();
		map.entrySet().parallelStream().forEach(entry -> {
			Arrays.asList(entry.getValue().getClass().getDeclaredMethods()).parallelStream()
					.filter(method -> method.getModifiers() == Modifier.PUBLIC).forEach(method -> {

						final String jOrchestraBeanName = entry.getKey();
						final Object jOrchestraBean = JOrchestraContextUtils.getJorchestraBean(jOrchestraBeanName);

						final JOrchestra jOrchestra = jOrchestraBean.getClass().getDeclaredAnnotation(JOrchestra.class);

						final String path = jOrchestra.path();
						final String methodName = method.getName();

						registerJOrchestraPath(registry, jOrchestraBeanName, method, path, methodName);
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

	private void registerJOrchestraPath(final WebSocketHandlerRegistry registry, final String jOrchestraBeanName,
			final Method method, final String path, final String methodName) {

		final String jorchestraPath = "/" + path + "-" + methodName;

		LOGGER.info("m=registerJOrchestraPath, jorchestraPath=" + jorchestraPath);

		final Config config = hazelCastConfig(jorchestraConfigurationProperties.getInstanceName());

		config.addExecutorConfig(createExecutorConfig(jorchestraPath));

		final HazelcastInstance hazelcastInstance = hazelcastInstance(config);

		final IExecutorService executorService = createExecutorService(jorchestraPath, hazelcastInstance);

		final JOrchestraWebSocketController jOrchestraWebSocketController = jOrchestraWebSocketController(
				jorchestraPath, jOrchestraBeanName, method, executorService);

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

	private Map<String, Object> loadJOrchestraBeans() {
		LOGGER.info("m=loadJOrchestraBeans, applicationContext.beanDefinitionNames="
				+ Arrays.toString(applicationContext.getBeanDefinitionNames()));

		final String[] jorcherstraBeans = applicationContext.getBeanNamesForAnnotation(JOrchestra.class);

		LOGGER.info("m=loadJOrchestraBeans, jorcherstraBeans=" + Arrays.toString(jorcherstraBeans));

		final Map<String, Object> map = new HashMap<>();

		Arrays.asList(jorcherstraBeans) //
				.parallelStream() //
				.forEach(jorcherstraBeanName -> {
					final Object object = applicationContext.getBean(jorcherstraBeanName);
					map.put(jorcherstraBeanName, object);
				});

		return map;
	}

	private JOrchestraWebSocketController jOrchestraWebSocketController(final String path,
			final String jOrchestraBeanName, final Method method, final IExecutorService executorService) {
		return new JOrchestraWebSocketController(path, jOrchestraBeanName, method, executorService);
	}
}
