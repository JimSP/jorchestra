package br.com.jorchestra.listener;

import java.io.IOException;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import br.com.jorchestra.controller.JOrchestraWebSocketController;

public class JOrchestraEventListenerCallback implements MessageListener<Object> {

	private JOrchestraWebSocketController jOrchestraWebSocketController;

	@Override
	public void onMessage(final Message<Object> message) {
		try {
			jOrchestraWebSocketController.onMessageCallback(message.getMessageObject());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setJOrchestraWebSocketController(final JOrchestraWebSocketController jOrchestraWebSocketController) {
		this.jOrchestraWebSocketController = jOrchestraWebSocketController;
	}
}
