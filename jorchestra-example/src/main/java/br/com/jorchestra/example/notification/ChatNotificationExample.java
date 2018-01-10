package br.com.jorchestra.example.notification;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;

import br.com.jorchestra.annotation.JOrchestra;
import br.com.jorchestra.canonical.JOrchestraSignal;
import br.com.jorchestra.dto.JOrchestraNotification;

//@JOrchestra(path = "chat", jOrchestraSignal = JOrchestraSignal.NOTIFICATION, reliable = true)
public class ChatNotificationExample {

	//@Autowired
	private HazelcastInstance hazelcastInstance;

	public void receive(final String message) throws JsonProcessingException {
		final ObjectMapper objectMapper = new ObjectMapper();
		final String payload = objectMapper.writeValueAsString(message);
		final JOrchestraNotification jOrchestraNotification = new JOrchestraNotification(String.class.getName(),
				payload);
		hazelcastInstance.getReliableTopic("/chat-receive").publish(jOrchestraNotification);
	}

}
