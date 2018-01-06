package br.com.jorchestra.example.system.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
import br.com.jorchestra.dto.JOrchestraSystemEvent;
import br.com.jorchestra.example.service.JOrchestraRegisterSystemEvents;

@Component
public class JOrchestraEventListener implements ApplicationListener<ApplicationEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraEventListener.class);

	@Autowired
	private JOrchestraRegisterSystemEvents jOrchestraRegisterSystemEvents;

	@Autowired
	private JOrchestraConfigurationProperties jOrchestraConfigurationProperties;

	@Override
	public void onApplicationEvent(final ApplicationEvent event) {
		LOGGER.info(
				"m=onApplicationEvent, jOrchestraName=" + jOrchestraConfigurationProperties.getName() + ", timestamp="
						+ event.getTimestamp() + ", class=" + event.getClass() + ", source=" + event.getSource());

		jOrchestraRegisterSystemEvents
				.lookUpAndAcceptIfPresent(new JOrchestraSystemEvent(event, jOrchestraConfigurationProperties));

	}
}
