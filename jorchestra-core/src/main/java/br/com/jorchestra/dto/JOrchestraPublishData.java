package br.com.jorchestra.dto;

import java.io.Serializable;

public class JOrchestraPublishData<T> implements Serializable{

	private static final long serialVersionUID = -1230834022301629448L;
	
	private final T data;

	public JOrchestraPublishData(final T data) {
		this.data = data;
	}

	public T getData() {
		return data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final JOrchestraPublishData<T> other = this.getClass().cast(obj);
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JOrchestraPublishData [data=" + data + "]";
	}
}
