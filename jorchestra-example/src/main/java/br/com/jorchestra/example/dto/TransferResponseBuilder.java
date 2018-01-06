package br.com.jorchestra.example.dto;

import java.util.UUID;

import br.com.jorchestra.example.canonical.Status;

public final class TransferResponseBuilder {

	public static TransferResponseBuilder create() {
		return new TransferResponseBuilder();
	}

	private TransferResponseBuilder() {

	}

	private UUID transferIdentification;
	private Status statusWithdraw;
	private Status statusTransfer;
	private TransferRequest transferRequest;

	/**
	 * nao usar prefixo with nesse caso. o prefixo with será chamado pelo
	 * JOrchestraHandle e tentará documentar esse campo.
	 * 
	 * Esse campo já é documentado pelo método withTransferIdentificationUUID.
	 * 
	 * @param transferIdentification
	 * @return TransferResponseBuilder
	 */
	public TransferResponseBuilder setTransferIdentification(final String transferIdentification) {
		this.transferIdentification = UUID.fromString(transferIdentification);
		return this;
	}

	public TransferResponseBuilder withTransferIdentificationUUID(final UUID transferIdentification) {
		this.transferIdentification = transferIdentification;
		return this;
	}

	public TransferResponseBuilder withStatusWithdraw(final Status statusWithdraw) {
		this.statusWithdraw = statusWithdraw;
		return this;
	}

	public TransferResponseBuilder withStatusTransfer(final Status statusTransfer) {
		this.statusTransfer = statusTransfer;
		return this;
	}

	public TransferResponseBuilder withTransferRequest(final TransferRequest transferRequest) {
		this.transferRequest = transferRequest;
		return this;
	}

	public TransferResponse build() {
		return new TransferResponse(transferIdentification, statusWithdraw, statusTransfer, transferRequest);
	}
}
