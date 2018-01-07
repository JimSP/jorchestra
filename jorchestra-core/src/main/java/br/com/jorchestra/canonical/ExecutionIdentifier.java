package br.com.jorchestra.canonical;

import java.io.Serializable;

public class ExecutionIdentifier implements Comparable<ExecutionIdentifier>, Serializable {

	private static final long serialVersionUID = -5738495538074280915L;

	private final String clusterName;
	private final String jOrchestraName;
	private final String sessionId;
	private final String requestId;

	public ExecutionIdentifier(String clusterName, String jOrchestraName, String sessionId, String requestId) {
		super();
		this.clusterName = clusterName;
		this.jOrchestraName = jOrchestraName;
		this.sessionId = sessionId;
		this.requestId = requestId;
	}

	public String getClusterName() {
		return clusterName;
	}

	public String getjOrchestraName() {
		return jOrchestraName;
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getRequestId() {
		return requestId;
	}

	@Override
	public int compareTo(ExecutionIdentifier o) {
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clusterName == null) ? 0 : clusterName.hashCode());
		result = prime * result + ((jOrchestraName == null) ? 0 : jOrchestraName.hashCode());
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
		ExecutionIdentifier other = (ExecutionIdentifier) obj;
		if (clusterName == null) {
			if (other.clusterName != null)
				return false;
		} else if (!clusterName.equals(other.clusterName))
			return false;
		if (jOrchestraName == null) {
			if (other.jOrchestraName != null)
				return false;
		} else if (!jOrchestraName.equals(other.jOrchestraName))
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
		return "ExecutionIdentifier [clusterName=" + clusterName + ", jOrchestraName=" + jOrchestraName + ", sessionId="
				+ sessionId + ", requestId=" + requestId + "]";
	}
}
