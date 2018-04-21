package br.com.jorchestra.canonical;

import java.io.Serializable;
import java.util.Comparator;

public final class JOrchestraHandle
		implements Serializable, Comparable<JOrchestraHandle>, Comparator<JOrchestraHandle> {

	private static final long serialVersionUID = 8662912680061296957L;

	private final String jOrchestraBeanName;
	private final String methodName;
	private final Class<?>[] jorchestraParametersType;
	private final String path;
	private final JOrchestraSignalType jOrchestraSignalType;
	private final Boolean reliable;
	private final String failOverMethodName;
	private final String address;
	private final Integer port;

	public JOrchestraHandle(final String jOrchestraBeanName, final String methodName,
			final Class<?>[] jorchestraParametersType, final String path, final JOrchestraSignalType jOrchestraSignalType,
			final Boolean reliable, final String failOverMethodName, final String address, final Integer port) {
		this.jOrchestraBeanName = jOrchestraBeanName;
		this.methodName = methodName;
		this.jorchestraParametersType = jorchestraParametersType;
		this.path = path;
		this.jOrchestraSignalType = jOrchestraSignalType;
		this.reliable = reliable;
		this.failOverMethodName = failOverMethodName;
		this.address = address;
		this.port = port;
	}

	public String getjOrchestraBeanName() {
		return jOrchestraBeanName;
	}

	public String getMethodName() {
		return methodName;
	}

	public Class<?>[] getJorchestraParametersType() {
		return jorchestraParametersType;
	}

	public String getPath() {
		return path;
	}

	public String getJOrchestraPath() {
		return String.format("/%s-%s", getPath(), getMethodName());
	}

	public JOrchestraSignalType getjOrchestraSignalType() {
		return jOrchestraSignalType;
	}

	public Boolean isReliable() {
		return reliable;
	}

	public Boolean getReliable() {
		return isReliable();
	}

	public String getFailOverMethodName() {
		return failOverMethodName;
	}

	public String getAddress() {
		return address;
	}

	public Integer getPort() {
		return port;
	}

	public String getJOrchestraMachineAddress() {
		return "ws://" + address + ":" + port + "/" + getJOrchestraPath();
	}

	@Override
	public int compareTo(final JOrchestraHandle jOrchestraHandle) {
		return this.port.compareTo(jOrchestraHandle.getPort()) + this.address.compareTo(jOrchestraHandle.getAddress())
				+ this.getJOrchestraPath().compareTo(jOrchestraHandle.getJOrchestraPath());
	}

	@Override
	public int compare(final JOrchestraHandle jOrchestraHandle0, final JOrchestraHandle jOrchestraHandle1) {
		return Integer.compare(jOrchestraHandle0.hashCode(), jOrchestraHandle1.hashCode());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((failOverMethodName == null) ? 0 : failOverMethodName.hashCode());
		result = prime * result + ((jOrchestraBeanName == null) ? 0 : jOrchestraBeanName.hashCode());
		result = prime * result + ((jOrchestraSignalType == null) ? 0 : jOrchestraSignalType.hashCode());
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		result = prime * result + ((reliable == null) ? 0 : reliable.hashCode());
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
		JOrchestraHandle other = (JOrchestraHandle) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (failOverMethodName == null) {
			if (other.failOverMethodName != null)
				return false;
		} else if (!failOverMethodName.equals(other.failOverMethodName))
			return false;
		if (jOrchestraBeanName == null) {
			if (other.jOrchestraBeanName != null)
				return false;
		} else if (!jOrchestraBeanName.equals(other.jOrchestraBeanName))
			return false;
		if (jOrchestraSignalType != other.jOrchestraSignalType)
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		if (reliable == null) {
			if (other.reliable != null)
				return false;
		} else if (!reliable.equals(other.reliable))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JOrchestraHandle [jOrchestraBeanName=" + jOrchestraBeanName + ", methodName=" + methodName
				+ ", jorchestraParametersType=" + jorchestraParametersType + ", path=" + path + ", jOrchestraSignalType="
				+ jOrchestraSignalType + ", reliable=" + reliable + ", failOverMethodName=" + failOverMethodName
				+ ", address=" + address + ", port=" + port + "]";
	}
}
