package br.com.jorchestra.example.dto;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.jorchestra.example.canonical.Status;

public final class TransferResponse implements Serializable {

	private static final long serialVersionUID = -6573632342509110244L;

	public static TransferResponseBuilder create() {
		return TransferResponseBuilder.create();
	}

	private final UUID transferIdentification;
	private final Status statusWithdraw;
	private final Status statusTransfer;
	private final TransferRequest transferRequest;

	public TransferResponse(final UUID transferIdentification, final Status statusWithdraw, final Status statusTransfer,
			final TransferRequest transferRequest) {
		this.transferIdentification = transferIdentification;
		this.statusWithdraw = statusWithdraw;
		this.statusTransfer = statusTransfer;
		this.transferRequest = transferRequest;
	}

	@JsonCreator
	public TransferResponse(@JsonProperty("transferIdentification") final String transferIdentification,
			@JsonProperty("statusWithdraw") final Status statusWithdraw,
			@JsonProperty("statusTransfer") final Status statusTransfer,
			@JsonProperty("transferRequest") final TransferRequest transferRequest) {
		this.transferIdentification = UUID.fromString(transferIdentification);
		this.statusWithdraw = statusWithdraw;
		this.statusTransfer = statusTransfer;
		this.transferRequest = transferRequest;
	}

	public UUID getTransferIdentification() {
		return transferIdentification;
	}

	public Status getStatusWithdraw() {
		return statusWithdraw;
	}

	public Status getStatusTransfer() {
		return statusTransfer;
	}

	public TransferRequest getTransferRequest() {
		return transferRequest;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((statusTransfer == null) ? 0 : statusTransfer.hashCode());
		result = prime * result + ((statusWithdraw == null) ? 0 : statusWithdraw.hashCode());
		result = prime * result + ((transferIdentification == null) ? 0 : transferIdentification.hashCode());
		result = prime * result + ((transferRequest == null) ? 0 : transferRequest.hashCode());
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
		TransferResponse other = (TransferResponse) obj;
		if (statusTransfer != other.statusTransfer)
			return false;
		if (statusWithdraw != other.statusWithdraw)
			return false;
		if (transferIdentification == null) {
			if (other.transferIdentification != null)
				return false;
		} else if (!transferIdentification.equals(other.transferIdentification))
			return false;
		if (transferRequest == null) {
			if (other.transferRequest != null)
				return false;
		} else if (!transferRequest.equals(other.transferRequest))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TransferResponse [transferIdentification=" + transferIdentification + ", statusWithdraw="
				+ statusWithdraw + ", statusTransfer=" + statusTransfer + ", transferRequest=" + transferRequest + "]";
	}
}
