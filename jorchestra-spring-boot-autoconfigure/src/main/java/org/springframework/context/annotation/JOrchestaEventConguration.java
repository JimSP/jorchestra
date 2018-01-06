package org.springframework.context.annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
import br.com.jorchestra.dto.JOrchestraSystemEvent;

@Configuration
@DependsOn(value = { "jOrchestraAutoConfiguration" })
public class JOrchestaEventConguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestaEventConguration.class);

	private static class SaledClass {
		@SuppressWarnings("unchecked")
		private static Class<List<Consumer<JOrchestraSystemEvent>>> EVENT_LISTENER_SAFE_HANDLE = (Class<List<Consumer<JOrchestraSystemEvent>>>) SaledClass
				.createClassTemplateEventLister().getClass();

		private static List<Consumer<JOrchestraSystemEvent>> createClassTemplateEventLister() {
			return new ArrayList<>();
		}
	}

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private JOrchestraConfigurationProperties jorchestraConfigurationProperties;

	@Bean
	public HazelcastInstance hazelcastInstance() {
		return Hazelcast.getHazelcastInstanceByName(jorchestraConfigurationProperties.getClusterName());
	}

	@Bean("jOrchestraEventMaps")
	public Map<Class<?>, List<Consumer<JOrchestraSystemEvent>>> jOrchestraEventMaps() {
		return Collections.synchronizedMap(hashMap());
	}

	@Bean
	public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
		final SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
		eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
		return eventMulticaster;
	}

	private Map<Class<?>, List<Consumer<JOrchestraSystemEvent>>> hashMap() {
		final Map<Class<?>, List<Consumer<JOrchestraSystemEvent>>> map = new HashMap<>();

		final List<Consumer<JOrchestraSystemEvent>> list = new ArrayList<>();
		list.add((event) -> {
			LOGGER.info("m=accpet, JOrchestraName=" + jorchestraConfigurationProperties.getName()
					+ ", msg=\"JOrchestra bye!");
			hazelcastInstance().shutdown();
		});
		
		map.put(ContextClosedEvent.class, list);

		final Map<String, List<String>> eventClassList = jorchestraConfigurationProperties.getEventsClassMap();
		eventClassList.entrySet() //
				.parallelStream() //
				.forEach(entry -> {
					entry.getValue() //
							.parallelStream() //
							.forEach(action -> {
								final List<Consumer<JOrchestraSystemEvent>> eventListenerList = applicationContext
										.getBean(action, SaledClass.EVENT_LISTENER_SAFE_HANDLE);
								try {
									map.put(Class.forName(entry.getKey()), eventListenerList);
								} catch (ClassNotFoundException e) {
									LOGGER.warn("m=jOrchestraEventMaps, msg=\"create eventMap not put" + entry.getKey()
											+ ", verify correct class name in of your application.properties file!", e);
								}
							});

				});

		return map;
	}

}