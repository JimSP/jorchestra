package org.springframework.context.annotation;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;

@Component
public class JOrchestraEventListener implements ApplicationListener<ApplicationEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraEventListener.class);

	@Autowired
	@Qualifier("jORquestraEventMaps")
	private Map<Class<?>, Consumer<ApplicationEvent>> jORquestraEventMaps;

	@Autowired
	private JOrchestraConfigurationProperties jorchestraConfigurationProperties;

	@Override
	public void onApplicationEvent(final ApplicationEvent event) {
		LOGGER.info(
				"m=onApplicationEvent, jOrquestraName=" + jorchestraConfigurationProperties.getName() + ", timestamp="
						+ event.getTimestamp() + ", class=" + event.getClass() + ", source=" + event.getSource());

		Optional.ofNullable(jORquestraEventMaps.get(event.getClass())).ifPresent(consumer -> {
			consumer.accept(event);
		});

	}
}
