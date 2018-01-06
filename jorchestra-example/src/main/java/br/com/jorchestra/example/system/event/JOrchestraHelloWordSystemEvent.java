package br.com.jorchestra.example.system.event;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

import br.com.jorchestra.annotation.JOrchestra;
import br.com.jorchestra.annotation.JOrchestraSignal;
import br.com.jorchestra.dto.JOrchestraSystemEvent;

@JOrchestra(path = "events", jOrchestraSignal = JOrchestraSignal.EVENT, reliable = JOrchestraHelloWordSystemEvent.RELIABLE)
public class JOrchestraHelloWordSystemEvent implements Consumer<JOrchestraSystemEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraHelloWordSystemEvent.class);

	protected static final boolean RELIABLE = true;

	@Autowired
	private HazelcastInstance hazelcastInstance;

	@Override
	public void accept(final JOrchestraSystemEvent jOrchestraSystemEvent) {
		LOGGER.info("m=accept, jOrchestraSystemEvent=" + jOrchestraSystemEvent);

		if (JOrchestraHelloWordSystemEvent.RELIABLE) {
			final ITopic<JOrchestraSystemEvent> topic = hazelcastInstance.getReliableTopic("/events-accept");
			topic.publish(jOrchestraSystemEvent);
		} else {
			final ITopic<JOrchestraSystemEvent> topic = hazelcastInstance.getTopic("/events-accept");
			topic.publish(jOrchestraSystemEvent);
		}
	}
}
