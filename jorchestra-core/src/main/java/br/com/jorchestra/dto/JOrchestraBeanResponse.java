package br.com.jorchestra.dto;

public class JOrchestraBeanResponse {

	public static JOrchestraMonitorResponseBuilder create() {
		return JOrchestraMonitorResponseBuilder.create();
	}

	private final String jOrchestraBeanName;
	private final String jOrchestraPath;
	private final String requestTemplate;
	private final String responseTemplate;
	private final String message;

	public JOrchestraBeanResponse(final String jOrchestraBeanName, final String jOrchestraPath,
			final String requestTemplate, final String responseTemplate, final String message) {
		this.jOrchestraBeanName = jOrchestraBeanName;
		this.jOrchestraPath = jOrchestraPath;
		this.requestTemplate = requestTemplate;
		this.responseTemplate = responseTemplate;
		this.message = message;
	}

	public String getjOrchestraBeanName() {
		return jOrchestraBeanName;
	}

	public String getjOrchestraPath() {
		return jOrchestraPath;
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
		return "JOrchestraBeanResponse [jOrchestraBeanName=" + jOrchestraBeanName + ", jOrchestraPath=" + jOrchestraPath
				+ ", requestTemplate=" + requestTemplate + ", responseTemplate=" + responseTemplate + ", message="
				+ message + "]";
	}
}
