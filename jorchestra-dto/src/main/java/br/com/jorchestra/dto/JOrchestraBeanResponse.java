package br.com.jorchestra.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JOrchestraBeanResponse {

	public static JOrchestraMonitorResponseBuilder create() {
		return JOrchestraMonitorResponseBuilder.create();
	}

	private final String jOrchestraBeanName;
	private final String jOrchestraPath;
	private final String requestTemplate;
	private final String responseTemplate;
	private final String message;

	@JsonCreator
	public JOrchestraBeanResponse(@JsonProperty("jOrchestraBeanName") final String jOrchestraBeanName,
			@JsonProperty("jOrchestraPath") final String jOrchestraPath,
			@JsonProperty("requestTemplate") final String requestTemplate,
			@JsonProperty("responseTemplate") final String responseTemplate,
			@JsonProperty("message") final String message) {
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((jOrchestraBeanName == null) ? 0 : jOrchestraBeanName.hashCode());
		result = prime * result + ((jOrchestraPath == null) ? 0 : jOrchestraPath.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((requestTemplate == null) ? 0 : requestTemplate.hashCode());
		result = prime * result + ((responseTemplate == null) ? 0 : responseTemplate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JOrchestraBeanResponse other = (JOrchestraBeanResponse) obj;
		if (jOrchestraBeanName == null) {
			if (other.jOrchestraBeanName != null)
				return false;
		} else if (!jOrchestraBeanName.equals(other.jOrchestraBeanName))
			return false;
		if (jOrchestraPath == null) {
			if (other.jOrchestraPath != null)
				return false;
		} else if (!jOrchestraPath.equals(other.jOrchestraPath))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (requestTemplate == null) {
			if (other.requestTemplate != null)
				return false;
		} else if (!requestTemplate.equals(other.requestTemplate))
			return false;
		if (responseTemplate == null) {
			if (other.responseTemplate != null)
				return false;
		} else if (!responseTemplate.equals(other.responseTemplate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JOrchestraBeanResponse [jOrchestraBeanName=" + jOrchestraBeanName + ", jOrchestraPath=" + jOrchestraPath
				+ ", requestTemplate=" + requestTemplate + ", responseTemplate=" + responseTemplate + ", message="
				+ message + "]";
	}
}
