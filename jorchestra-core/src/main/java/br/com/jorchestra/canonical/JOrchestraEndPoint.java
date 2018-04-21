package br.com.jorchestra.canonical;

import java.io.Serializable;

public class JOrchestraEndPoint implements Serializable {

	private static final long serialVersionUID = 2491295120608207746L;

	private final String clusterName;
	private final String jOrchestraName;
	private final String jOrchestraPath;
	private final String jOrchestraMachineAddress;
	private final Integer jOrchestraMachinePort;

	public JOrchestraEndPoint(final String clusterName, final String jOrchestraName, final String jOrchestraPath,
			final String jOrchestraMachineAddress, final Integer jOrchestraMachinePort) {
		this.clusterName = clusterName;
		this.jOrchestraName = jOrchestraName;
		this.jOrchestraPath = jOrchestraPath;
		this.jOrchestraMachineAddress = jOrchestraMachineAddress;
		this.jOrchestraMachinePort = jOrchestraMachinePort;
	}

	public String getClusterName() {
		return clusterName;
	}

	public String getjOrchestraName() {
		return jOrchestraName;
	}

	public String getjOrchestraPath() {
		return jOrchestraPath;
	}

	public String getjOrchestraMachineAddress() {
		return jOrchestraMachineAddress;
	}

	public Integer getjOrchestraMachinePort() {
		return jOrchestraMachinePort;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clusterName == null) ? 0 : clusterName.hashCode());
		result = prime * result + ((jOrchestraMachineAddress == null) ? 0 : jOrchestraMachineAddress.hashCode());
		result = prime * result + ((jOrchestraMachinePort == null) ? 0 : jOrchestraMachinePort.hashCode());
		result = prime * result + ((jOrchestraName == null) ? 0 : jOrchestraName.hashCode());
		result = prime * result + ((jOrchestraPath == null) ? 0 : jOrchestraPath.hashCode());
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
		JOrchestraEndPoint other = (JOrchestraEndPoint) obj;
		if (clusterName == null) {
			if (other.clusterName != null)
				return false;
		} else if (!clusterName.equals(other.clusterName))
			return false;
		if (jOrchestraMachineAddress == null) {
			if (other.jOrchestraMachineAddress != null)
				return false;
		} else if (!jOrchestraMachineAddress.equals(other.jOrchestraMachineAddress))
			return false;
		if (jOrchestraMachinePort == null) {
			if (other.jOrchestraMachinePort != null)
				return false;
		} else if (!jOrchestraMachinePort.equals(other.jOrchestraMachinePort))
			return false;
		if (jOrchestraName == null) {
			if (other.jOrchestraName != null)
				return false;
		} else if (!jOrchestraName.equals(other.jOrchestraName))
			return false;
		if (jOrchestraPath == null) {
			if (other.jOrchestraPath != null)
				return false;
		} else if (!jOrchestraPath.equals(other.jOrchestraPath))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JOrchestraEndPoint [clusterName=" + clusterName + ", jOrchestraName=" + jOrchestraName
				+ ", jOrchestraPath=" + jOrchestraPath + ", jOrchestraMachineAddress=" + jOrchestraMachineAddress
				+ ", jOrchestraMachinePort=" + jOrchestraMachinePort + "]";
	}
}
