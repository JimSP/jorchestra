
package br.com.jorchestra.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import br.com.jorchestra.example.client.JOrchestraClientExample;

public class JOrchestraWebSocketClientHandle extends TextWebSocketHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraClientExample.class);

	private boolean handleTransportError;

	private boolean handleTextMessage;

	private boolean afterConnectionEstablished;

	private boolean afterConnectionClosed;

	public boolean isHandleTransportError() {
		return handleTransportError;
	}

	public void setHandleTransportError(boolean handleTransportError) {
		this.handleTransportError = handleTransportError;
	}

	public boolean isHandleTextMessage() {
		return handleTextMessage;
	}

	public void setHandleTextMessage(boolean handleTextMessage) {
		this.handleTextMessage = handleTextMessage;
	}

	public boolean isAfterConnectionEstablished() {
		return afterConnectionEstablished;
	}

	public void setAfterConnectionEstablished(boolean afterConnectionEstablished) {
		this.afterConnectionEstablished = afterConnectionEstablished;
	}

	public boolean isAfterConnectionClosed() {
		return afterConnectionClosed;
	}

	public void setAfterConnectionClosed(boolean afterConnectionClosed) {
		this.afterConnectionClosed = afterConnectionClosed;
	}

	@Override
	public void handleTransportError(final WebSocketSession session, final Throwable exception) throws Exception {
		LOGGER.info("m=handleTransportError, session.id=" + session.getId(), exception);
		handleTransportError = true;
	}

	@Override
	public void handleTextMessage(final WebSocketSession session, final TextMessage message) throws Exception {
		LOGGER.info("m=handleMessage, session.id=" + session.getId() + "message.payload=" + message.getPayload());
		handleTextMessage = true;
	}

	@Override
	public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
		LOGGER.info("m=afterConnectionEstablished, session.id=" + session.getId());
		afterConnectionEstablished = true;
	}

	@Override
	public void afterConnectionClosed(final WebSocketSession session, final CloseStatus closeStatus) throws Exception {
		LOGGER.info("m=afterConnectionClosed, session.id=" + session.getId() + ", closeStatus=" + closeStatus);
		afterConnectionClosed = true;
	}

}