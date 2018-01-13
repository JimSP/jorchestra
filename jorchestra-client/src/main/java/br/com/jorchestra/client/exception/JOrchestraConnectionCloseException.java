package br.com.jorchestra.client.exception;

import javax.websocket.CloseReason;

public class JOrchestraConnectionCloseException extends Exception {

	private static final long serialVersionUID = 8260787459018599470L;

	private final CloseReason closeReason;

	public JOrchestraConnectionCloseException(final CloseReason closeReason) {
		this.closeReason = closeReason;
	}

	public CloseReason getCloseReason() {
		return closeReason;
	}

	@Override
	public String toString() {
		return "JOrchestraConnectionCloseException [closeReason=" + closeReason + "]";
	}
}
