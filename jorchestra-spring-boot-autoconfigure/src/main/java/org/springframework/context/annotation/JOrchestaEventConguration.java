package org.springframework.context.annotation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;

@Configuration
@DependsOn(value = { "jOrchestraAutoConfiguration" })
public class JOrchestaEventConguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestaEventConguration.class);

	private static class SaledClass {
		@SuppressWarnings("unchecked")
		private static Class<Consumer<ApplicationEvent>> EVENT_LISTENER_SAFE_HANDLE = (Class<Consumer<ApplicationEvent>>) SaledClass
				.createClassTemplateEventLister().getClass();

		private static Consumer<ApplicationEvent> createClassTemplateEventLister() {
			return (event) -> {
				throw new RuntimeException("not executable!");
			};
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

	@Bean("jORquestraEventMaps")
	public Map<Class<?>, Consumer<ApplicationEvent>> jORquestraEventMaps() {
		return Collections.synchronizedMap(hashMap());
	}

	@Bean
	public JOrchestraEventListener jOrchestraEventListener() {
		return new JOrchestraEventListener();
	}

	@Bean
	public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
		final SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
		eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
		return eventMulticaster;
	}

	private Map<Class<?>, Consumer<ApplicationEvent>> hashMap() {
		final Map<Class<?>, Consumer<ApplicationEvent>> map = new HashMap<>();
		map.put(ContextClosedEvent.class, (event) -> {
			LOGGER.info("m=accpet, JOrquestraName=" + jorchestraConfigurationProperties.getName() + ", msg=\"JOrchestra bye!");
			hazelcastInstance().shutdown();
		});

		final Map<String, String> eventClassList = jorchestraConfigurationProperties.getEventsClassMap();
		eventClassList.entrySet().iterator().forEachRemaining(entry -> {
			final Consumer<ApplicationEvent> eventListener = applicationContext.getBean(entry.getValue(),
					SaledClass.EVENT_LISTENER_SAFE_HANDLE);
			try {
				map.put(Class.forName(entry.getKey()), eventListener);
			} catch (ClassNotFoundException e) {
				LOGGER.warn("m=jORquestraEventMaps, msg=\"create eventMap not put" + entry.getKey()
						+ ", verify correct class name in of your application.properties file!", e);
			}
		});

		return map;
	}

}
