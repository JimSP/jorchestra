package br.com.jorchestra.dto;

public final class JOrquestraMonitorResponseBuilder {

	public static JOrquestraMonitorResponseBuilder create() {
		return new JOrquestraMonitorResponseBuilder();
	}

	private String jOrchestraBeanName;
	private String jOrchestraPah;
	private String requestTemplate;
	private String responseTemplate;

	private JOrquestraMonitorResponseBuilder() {

	}

	public JOrquestraMonitorResponseBuilder withjOrchestraBeanName(final String jOrchestraBeanName) {
		this.jOrchestraBeanName = jOrchestraBeanName;
		return this;
	}

	public JOrquestraMonitorResponseBuilder withjOrchestraPah(final String jOrchestraPah) {
		this.jOrchestraPah = jOrchestraPah;
		return this;
	}

	public JOrquestraMonitorResponseBuilder withRequestTemplate(final String requestTemplate) {
		this.requestTemplate = requestTemplate;
		return this;
	}

	public JOrquestraMonitorResponseBuilder withResponseTemplate(final String responseTemplate) {
		this.responseTemplate = responseTemplate;
		return this;
	}

	public JOrquestraBeanResponse build() {
		return new JOrquestraBeanResponse(jOrchestraBeanName, jOrchestraPah, requestTemplate, responseTemplate);
	}
}
