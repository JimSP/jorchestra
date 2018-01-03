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

	public TransferResponseBuilder withTransferIdentification(final String transferIdentification) {
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

	public TransferResponse build() {
		return new TransferResponse(transferIdentification, statusWithdraw, statusTransfer);
	}
}
