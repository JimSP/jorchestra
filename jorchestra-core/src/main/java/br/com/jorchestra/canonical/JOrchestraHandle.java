package br.com.jorchestra.canonical;

import java.io.Serializable;
import java.util.Arrays;

public final class JOrchestraHandle implements Serializable {

	private static final long serialVersionUID = 8662912680061296957L;

	private final String jOrchestraBeanName;
	private final String methodName;
	private final Class<?>[] jorchestraParametersType;
	private final String path;
	private final JOrchestraSignal jOrchestraSignal;
	private final Boolean reliable;

	public JOrchestraHandle(final String jOrchestraBeanName, final String methodName,
			final Class<?>[] jorchestraParametersType, final String path, final JOrchestraSignal jOrchestraSignal,
			final Boolean reliable) {
		this.jOrchestraBeanName = jOrchestraBeanName;
		this.methodName = methodName;
		this.jorchestraParametersType = jorchestraParametersType;
		this.path = path;
		this.jOrchestraSignal = jOrchestraSignal;
		this.reliable = reliable;
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

	public JOrchestraSignal getjOrchestraSignal() {
		return jOrchestraSignal;
	}

	public Boolean isReliable() {
		return reliable;
	}

	public Boolean getReliable() {
		return isReliable();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((jOrchestraBeanName == null) ? 0 : jOrchestraBeanName.hashCode());
		result = prime * result + ((jOrchestraSignal == null) ? 0 : jOrchestraSignal.hashCode());
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		if (jOrchestraBeanName == null) {
			if (other.jOrchestraBeanName != null)
				return false;
		} else if (!jOrchestraBeanName.equals(other.jOrchestraBeanName))
			return false;
		if (jOrchestraSignal != other.jOrchestraSignal)
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
				+ ", jorchestraParametersType=" + Arrays.toString(jorchestraParametersType) + ", path=" + path
				+ ", jOrchestraSignal=" + jOrchestraSignal + ", reliable=" + reliable + "]";
	}
}
