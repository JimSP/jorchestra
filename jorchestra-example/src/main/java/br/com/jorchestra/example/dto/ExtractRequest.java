package br.com.jorchestra.example.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.jorchestra.example.canonical.Account;

public class ExtractRequest implements Serializable {

	private static final long serialVersionUID = 4614342125283998014L;

	private final Account account;
	private final Period period;

	@JsonCreator
	public ExtractRequest(@JsonProperty("account") final Account account, @JsonProperty("period") final Period period) {
		super();
		this.account = account;
		this.period = period;
	}

	public Account getAccount() {
		return account;
	}

	public Period getPeriod() {
		return period;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		result = prime * result + ((period == null) ? 0 : period.hashCode());
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
		ExtractRequest other = (ExtractRequest) obj;
		if (account == null) {
			if (other.account != null)
				return false;
		} else if (!account.equals(other.account))
			return false;
		if (period == null) {
			if (other.period != null)
				return false;
		} else if (!period.equals(other.period))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExtractRequest [account=" + account + ", period=" + period + "]";
	}
}
