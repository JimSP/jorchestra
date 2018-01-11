package br.com.jorchestra.dto;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.jorchestra.canonical.JOrchestraCommand;
import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;

public class JOrchestraAdminRequest implements Serializable {

	private static final long serialVersionUID = 6465352633027446288L;

	public static boolean isValidUserNameAndPassword(final JOrchestraAdminRequest jOrchestraAdminRequest,
			final JOrchestraConfigurationProperties jOrchestraConfigurationProperties) {
		return jOrchestraConfigurationProperties.getUsername().equals(jOrchestraAdminRequest.getUsername())
				&& jOrchestraConfigurationProperties.getPassword()
						.equals(jOrchestraConfigurationProperties.getPassword());
	}

	private final JOrchestraCommand jOrchestraCommand;
	private final String jOrchestaPath;
	private final String sessionId;
	private final String requestId;
	private final String username;
	private final String password;
	private final Map<String, String> extraData;

	@JsonCreator
	public JOrchestraAdminRequest(@JsonProperty("jOrchestraCommand") final JOrchestraCommand jOrchestraCommand,
			@JsonProperty("jOrchestaPath") final String jOrchestaPath,
			@JsonProperty("sessionId") final String sessionId, @JsonProperty("requestId") final String requestId,
			@JsonProperty("username") final String username, @JsonProperty("password") final String password,
			@JsonProperty("extraData") final Map<String, String> extraData) {
		this.jOrchestraCommand = jOrchestraCommand;
		this.jOrchestaPath = jOrchestaPath;
		this.sessionId = sessionId;
		this.requestId = requestId;
		this.username = username;
		this.password = password;
		this.extraData = extraData;
	}

	@JsonProperty("jOrchestraCommand")
	public JOrchestraCommand getJOrchestraCommand() {
		return jOrchestraCommand;
	}

	@JsonProperty("jOrchestaPath")
	public String getJOrchestaPath() {
		return jOrchestaPath;
	}

	@JsonProperty("sessionId")
	public String getSessionId() {
		return sessionId;
	}

	@JsonProperty("requestId")
	public String getRequestId() {
		return requestId;
	}

	@JsonProperty("username")
	public String getUsername() {
		return username;
	}

	@JsonProperty("password")
	public String getPassword() {
		return password;
	}
	
	@JsonProperty("extraData")
	public Map<String, String> getExtraData() {
		return extraData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((jOrchestraCommand == null) ? 0 : jOrchestraCommand.hashCode());
		result = prime * result + ((jOrchestaPath == null) ? 0 : jOrchestaPath.hashCode());
		result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
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
		JOrchestraAdminRequest other = (JOrchestraAdminRequest) obj;
		if (jOrchestraCommand != other.jOrchestraCommand)
			return false;
		if (jOrchestaPath == null) {
			if (other.jOrchestaPath != null)
				return false;
		} else if (!jOrchestaPath.equals(other.jOrchestaPath))
			return false;
		if (requestId == null) {
			if (other.requestId != null)
				return false;
		} else if (!requestId.equals(other.requestId))
			return false;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JOrchestraAdminRequest [jOrchestraCommand=" + jOrchestraCommand + ", jOrchestaPath=" + jOrchestaPath
				+ ", sessionId=" + sessionId + ", requestId=" + requestId + "]";
	}
}
