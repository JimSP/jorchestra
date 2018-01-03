package br.com.jorchestra.example.dto;

import java.math.BigDecimal;

import br.com.jorchestra.example.canonical.Account;

public final class TransferRequestBuilder {

	public static TransferRequestBuilder create() {
		return new TransferRequestBuilder();
	}

	private TransferRequestBuilder() {

	}

	private Account from;
	private Account to;
	private BigDecimal value;

	public TransferRequestBuilder withFrom(final Account from) {
		this.from = from;
		return this;
	}

	public TransferRequestBuilder withTo(final Account to) {
		this.to = to;
		return this;
	}

	public TransferRequestBuilder withValue(final BigDecimal value) {
		this.value = value;
		return this;
	}

	public TransferRequest build() {
		return new TransferRequest(from, to, value);
	}
}
