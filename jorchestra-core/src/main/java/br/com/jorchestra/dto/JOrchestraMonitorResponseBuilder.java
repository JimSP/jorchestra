package br.com.jorchestra.dto;

public final class JOrchestraMonitorResponseBuilder {

	public static JOrchestraMonitorResponseBuilder create() {
		return new JOrchestraMonitorResponseBuilder();
	}

	private String jOrchestraBeanName;
	private String jOrchestraPah;
	private String requestTemplate;
	private String responseTemplate;

	private JOrchestraMonitorResponseBuilder() {

	}

	public JOrchestraMonitorResponseBuilder withjOrchestraBeanName(final String jOrchestraBeanName) {
		this.jOrchestraBeanName = jOrchestraBeanName;
		return this;
	}

	public JOrchestraMonitorResponseBuilder withjOrchestraPah(final String jOrchestraPah) {
		this.jOrchestraPah = jOrchestraPah;
		return this;
	}

	public JOrchestraMonitorResponseBuilder withRequestTemplate(final String requestTemplate) {
		this.requestTemplate = requestTemplate;
		return this;
	}

	public JOrchestraMonitorResponseBuilder withResponseTemplate(final String responseTemplate) {
		this.responseTemplate = responseTemplate;
		return this;
	}

	public JOrchestraBeanResponse build() {
		return new JOrchestraBeanResponse(jOrchestraBeanName, jOrchestraPah, requestTemplate, responseTemplate);
	}
}
