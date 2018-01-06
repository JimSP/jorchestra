package br.com.jorchestra.example.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

import br.com.jorchestra.annotation.JOrchestra;
import br.com.jorchestra.canonical.JOrchestraSignal;
import br.com.jorchestra.dto.JOrchestraNotification;
import br.com.jorchestra.example.canonical.Status;
import br.com.jorchestra.example.dto.TransferResponse;

@JOrchestra(path = "notification", jOrchestraSignal = JOrchestraSignal.NOTIFICATION, reliable = JOrchestraNotificationEletronicTransferAccount.RELIABLE)
public class JOrchestraNotificationEletronicTransferAccount {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraNotificationEletronicTransferAccount.class);

	protected static final boolean RELIABLE = false;

	@Autowired
	@Qualifier("hazelcastInstance")
	private HazelcastInstance hazelcastInstance;

	public Status account(final TransferResponse transferResponse) {
		LOGGER.debug("m=account, transferResponse=" + transferResponse);

		try {
			final ObjectMapper objectMapper = new ObjectMapper();
			final byte[] messageData = objectMapper.writeValueAsBytes(transferResponse);

			if (JOrchestraNotificationEletronicTransferAccount.RELIABLE) {
				final ITopic<JOrchestraNotification> topic = hazelcastInstance
						.getReliableTopic("/notification-account");
				topic.publish(new JOrchestraNotification(TransferResponse.class.getName(), messageData));
			} else {
				final ITopic<JOrchestraNotification> topic = hazelcastInstance.getTopic("/notification-account");
				topic.publish(new JOrchestraNotification(TransferResponse.class.getName(), messageData));
			}

			return Status.SUCCESS;
		} catch (Throwable e) {
			LOGGER.error("m=account, transferResponse=" + transferResponse, e);
			return Status.ERROR;
		}
	}
}
