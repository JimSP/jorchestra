package br.com.jorchestra.example.system.event;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

@Component
public class JOrquestraHelloWordSystemEvent implements Consumer<ApplicationEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrquestraHelloWordSystemEvent.class);
	
	@Override
	public void accept(final ApplicationEvent applicationEvent) {
		LOGGER.info("JOrquestra intercepter spring-boot event!");
	}
}
