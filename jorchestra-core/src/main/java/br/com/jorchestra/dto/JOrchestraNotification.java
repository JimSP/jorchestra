package br.com.jorchestra.dto;

import java.io.Serializable;
import java.util.Arrays;

public class JOrchestraNotification implements Serializable {

	private static final long serialVersionUID = -8799557113566292444L;

	private final String type;
	private final byte[] payload;

	public JOrchestraNotification(final String type, final byte[] payload) {
		this.type = type;
		this.payload = payload;
	}

	public String getType() {
		return type;
	}

	public byte[] getPayload() {
		return payload;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(payload);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		JOrchestraNotification other = (JOrchestraNotification) obj;
		if (!Arrays.equals(payload, other.payload))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JOrchestraNotification [type=" + type + ", payload=" + Arrays.toString(payload) + "]";
	}
}
