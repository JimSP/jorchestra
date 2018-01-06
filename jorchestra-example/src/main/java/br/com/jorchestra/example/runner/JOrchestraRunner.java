package br.com.jorchestra.example.runner;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;

import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
import br.com.jorchestra.example.service.JOrchestraRegisterSystemEvents;
import br.com.jorchestra.example.system.event.JOrchestraHelloWordSystemEvent;
import br.com.jorchestra.service.JOrchestraBeans;

@Component
public class JOrchestraRunner implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraRunner.class);

	@Autowired
	private JOrchestraConfigurationProperties jorchestraConfigurationProperties;

	@Autowired
	private JOrchestraBeans JOrchestraBeans;

	@Autowired
	private JOrchestraHelloWordSystemEvent jOrchestraHelloWordSystemEvent;

	@Autowired
	@Qualifier("jOrchestraRegisterSystemEvents")
	private JOrchestraRegisterSystemEvents jOrchestraRegisterSystemEvents;

	@Override
	public void run(String... args) throws Exception {
		LOGGER.info("m=run, args=" + Arrays.toString(args));
		LOGGER.info("m=run, jorchestraConfigurationProperties=" + jorchestraConfigurationProperties);
		LOGGER.info("m=run, JOrchestraBeans=" + Arrays.toString(JOrchestraBeans.beans().toArray()));

		jOrchestraRegisterSystemEvents.addConsumer(ServletRequestHandledEvent.class, jOrchestraHelloWordSystemEvent);
		jOrchestraRegisterSystemEvents.addConsumer(ContextRefreshedEvent.class, jOrchestraHelloWordSystemEvent);

	}
}
