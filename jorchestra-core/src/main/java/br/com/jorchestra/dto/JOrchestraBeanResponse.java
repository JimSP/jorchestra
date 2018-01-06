package br.com.jorchestra.dto;

public class JOrchestraBeanResponse {

	public static JOrchestraMonitorResponseBuilder create() {
		return JOrchestraMonitorResponseBuilder.create();
	}

	private final String jOrchestraBeanName;
	private final String jOrchestraPah;
	private final String requestTemplate;
	private final String responseTemplate;
	private final String message;

	public JOrchestraBeanResponse(final String jOrchestraBeanName, final String jOrchestraPah,
			final String requestTemplate, final String responseTemplate, final String message) {
		this.jOrchestraBeanName = jOrchestraBeanName;
		this.jOrchestraPah = jOrchestraPah;
		this.requestTemplate = requestTemplate;
		this.responseTemplate = responseTemplate;
		this.message = message;
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

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "JOrchestraBeanResponse [jOrchestraBeanName=" + jOrchestraBeanName + ", jOrchestraPah=" + jOrchestraPah
				+ ", requestTemplate=" + requestTemplate + ", responseTemplate=" + responseTemplate + ", message="
				+ message + "]";
	}
}
