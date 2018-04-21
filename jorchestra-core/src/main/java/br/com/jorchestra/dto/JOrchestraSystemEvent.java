package br.com.jorchestra.dto;

import org.springframework.context.ApplicationEvent;

import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;

public class JOrchestraSystemEvent extends ApplicationEvent {

	private static final long serialVersionUID = 4533153975213988068L;

	private final JOrchestraConfigurationProperties jOrchestraConfigurationProperties;
	
	public JOrchestraSystemEvent(final ApplicationEvent event,
			final JOrchestraConfigurationProperties jOrchestraConfigurationProperties) {
		super(event.getSource());
		this.jOrchestraConfigurationProperties = jOrchestraConfigurationProperties;
	}

	public JOrchestraConfigurationProperties getjOrchestraConfigurationProperties() {
		return jOrchestraConfigurationProperties;
	}

	@Override
	public String toString() {
		return "JOrchestraSystemEvent [jOrchestraConfigurationProperties=" + jOrchestraConfigurationProperties
				+ ", " + super.toString() + "]";
	}
}
