package br.com.jorchestra.runtime;

import org.springframework.web.socket.WebSocketSession;

public interface RuntimeCallback {

	void sendMessage(final WebSocketSession webSocketSession, final String string, final Object... result);

}
