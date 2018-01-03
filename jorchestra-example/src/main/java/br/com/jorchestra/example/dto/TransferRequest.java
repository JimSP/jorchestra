package br.com.jorchestra.example.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.jorchestra.example.canonical.Account;

public final class TransferRequest implements Serializable {

	private static final long serialVersionUID = -3986884370817212410L;

	public static TransferRequestBuilder create() {
		return TransferRequestBuilder.create();
	}

	private final UUID transferIdentification;
	private final Account from;
	private final Account to;
	private final BigDecimal value;

	@JsonCreator
	public TransferRequest(@JsonProperty("from") final Account from, @JsonProperty("to") final Account to,
			@JsonProperty("value") final BigDecimal value) {
		this.transferIdentification = UUID.randomUUID();
		this.from = from;
		this.to = to;
		this.value = value;
	}

	@JsonCreator
	public TransferRequest(@JsonProperty("transferIdentification") final String transferIdentification,
			@JsonProperty("from") final Account from, @JsonProperty("to") final Account to,
			@JsonProperty("value") final BigDecimal value) {
		this.transferIdentification = UUID.fromString(transferIdentification);
		this.from = from;
		this.to = to;
		this.value = value;
	}

	public String getTransferIdentification() {
		return transferIdentification.toString();
	}

	@JsonIgnore
	public UUID getTransferIdentificationUUID() {
		return transferIdentification;
	}

	public Account getFrom() {
		return from;
	}

	public Account getTo() {
		return to;
	}

	public BigDecimal getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		result = prime * result + ((transferIdentification == null) ? 0 : transferIdentification.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		TransferRequest other = (TransferRequest) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		if (transferIdentification == null) {
			if (other.transferIdentification != null)
				return false;
		} else if (!transferIdentification.equals(other.transferIdentification))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TransferRequest [transferIdentification=" + transferIdentification + ", from=" + from + ", to=" + to
				+ ", value=" + value + "]";
	}
}
