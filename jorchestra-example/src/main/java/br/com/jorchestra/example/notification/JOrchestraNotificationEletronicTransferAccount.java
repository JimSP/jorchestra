package br.com.jorchestra.example.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

import br.com.jorchestra.annotation.JOrchestra;
import br.com.jorchestra.canonical.JOrchestraSignal;
import br.com.jorchestra.dto.JOrchestraNotification;
import br.com.jorchestra.example.canonical.Status;
import br.com.jorchestra.example.dto.TransferResponse;

@JOrchestra(path = "notification", jOrchestraSignalType = JOrchestraSignal.NOTIFICATION, reliable = JOrchestraNotificationEletronicTransferAccount.RELIABLE)
public class JOrchestraNotificationEletronicTransferAccount {

	private static final String NOTIFICATION_ACCOUNT = "/notification-account";

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraNotificationEletronicTransferAccount.class);

	protected static final boolean RELIABLE = false;

	@Autowired
	private HazelcastInstance hazelcastInstance;

	public Status account(final TransferResponse transferResponse) {
		LOGGER.debug("m=account, transferResponse=" + transferResponse);

		try {
			final String messageData = toJson(transferResponse);

			if (JOrchestraNotificationEletronicTransferAccount.RELIABLE) {
				final ITopic<JOrchestraNotification> topic = hazelcastInstance
						.getReliableTopic(NOTIFICATION_ACCOUNT);
				publish(messageData, topic);
			} else {
				final ITopic<JOrchestraNotification> topic = hazelcastInstance.getTopic(NOTIFICATION_ACCOUNT);
				publish(messageData, topic);
			}

			return Status.SUCCESS;
		} catch (Throwable e) {
			LOGGER.error("m=account, transferResponse=" + transferResponse, e);
			return Status.ERROR;
		}
	}

	private String toJson(final TransferResponse transferResponse) throws JsonProcessingException {
		final ObjectMapper objectMapper = new ObjectMapper();
		final String messageData = objectMapper.writeValueAsString(transferResponse);
		return messageData;
	}

	private void publish(final String messageData, final ITopic<JOrchestraNotification> topic) {
		topic.publish(new JOrchestraNotification(TransferResponse.class.getName(), messageData));
	}
}
