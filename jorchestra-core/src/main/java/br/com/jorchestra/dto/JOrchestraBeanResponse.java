package br.com.jorchestra.dto;

public class JOrchestraBeanResponse {
	
	public static JOrchestraMonitorResponseBuilder create() {
		return JOrchestraMonitorResponseBuilder.create();
	}

	private final String jOrchestraBeanName;
	private final String jOrchestraPah;
	private final String requestTemplate;
	private final String responseTemplate;

	public JOrchestraBeanResponse(final String jOrchestraBeanName, final String jOrchestraPah, final String requestTemplate, final String responseTemplate) {
		super();
		this.jOrchestraBeanName = jOrchestraBeanName;
		this.jOrchestraPah = jOrchestraPah;
		this.requestTemplate = requestTemplate;
		this.responseTemplate = responseTemplate;
	}

	public String getjOrchestraBeanName() {
		return jOrchestraBeanName;
	}

	public String getjOrchestraPah() {
		return jOrchestraPah;
	}

	public String getRequestTemplate() {
		return requestTemplate;
	}

	public String getResponseTemplate() {
		return responseTemplate;
	}

	@Override
	public String toString() {
		return "JOrchestraBeanResponse [jOrchestraBeanName=" + jOrchestraBeanName + ", jOrchestraPah="
				+ jOrchestraPah + ", requestTemplate=" + requestTemplate + ", responseTemplate=" + responseTemplate
				+ "]";
	}
}
