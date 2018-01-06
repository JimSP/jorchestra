package br.com.jorchestra.example.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
import br.com.jorchestra.dto.JOrchestraSystemEvent;

@Service("jOrchestraRegisterSystemEvents")
public class JOrchestraRegisterSystemEvents {

	@Autowired
	@Qualifier("jOrchestraEventMaps")
	private Map<Class<?>, List<Consumer<JOrchestraSystemEvent>>> jOrchestraEventMaps;

	@Autowired
	private JOrchestraConfigurationProperties jOrchestraConfigurationProperties;

	public void lookUpAndAcceptIfPresent(final JOrchestraSystemEvent event) {
		Optional.ofNullable(jOrchestraEventMaps.get(event.getClass())).ifPresent(consumer -> {
			consumer.parallelStream().forEach(
					action -> action.accept(new JOrchestraSystemEvent(event, jOrchestraConfigurationProperties)));
		});
	}

	public void addConsumer(final Class<?> clazz, final Consumer<JOrchestraSystemEvent> consumer) {
		if (jOrchestraEventMaps.containsKey(clazz)) {
			jOrchestraEventMaps.get(clazz).add(consumer);
		} else {
			final List<Consumer<JOrchestraSystemEvent>> list = Collections.synchronizedList(new ArrayList<>());
			list.add(consumer);
			jOrchestraEventMaps.put(clazz, list);
		}
	}
}
