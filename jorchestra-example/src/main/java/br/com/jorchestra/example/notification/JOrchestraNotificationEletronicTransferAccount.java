package br.com.jorchestra.example.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

import br.com.jorchestra.annotation.JOrchestra;
import br.com.jorchestra.annotation.JOrchestraSignal;
import br.com.jorchestra.dto.JOrchestraNotification;
import br.com.jorchestra.example.dto.TransferResponse;

@JOrchestra(path = "notification", jOrchestraSignal = JOrchestraSignal.NOTIFICATION, reliable = JOrchestraNotificationEletronicTransferAccount.RELIABLE)
public class JOrchestraNotificationEletronicTransferAccount {

	protected static final boolean RELIABLE = false;

	@Autowired
	@Qualifier("hazelcastInstance")
	private HazelcastInstance hazelcastInstance;

	public void account(final TransferResponse transferResponse) throws JsonProcessingException {

		final ObjectMapper objectMapper = new ObjectMapper();
		final byte[] messageData = objectMapper.writeValueAsBytes(transferResponse);

		if (JOrchestraNotificationEletronicTransferAccount.RELIABLE) {
			final ITopic<JOrchestraNotification> topic = hazelcastInstance.getReliableTopic("/notification-account");
			topic.publish(new JOrchestraNotification(TransferResponse.class.getName(), messageData));
		} else {
			final ITopic<JOrchestraNotification> topic = hazelcastInstance.getTopic("/notification-account");
			topic.publish(new JOrchestraNotification(TransferResponse.class.getName(), messageData));
		}
	}
}
