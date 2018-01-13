package br.com.jorchestra.client.exception;

public class JOrchestraConnectionErrorException extends Exception{

	private static final long serialVersionUID = 2638798890275591180L;

	public JOrchestraConnectionErrorException(final Throwable t) {
		super(t);
	}
}
