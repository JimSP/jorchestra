package br.com.jorchestra.example.dto;

import java.io.Serializable;
import java.util.UUID;

import br.com.jorchestra.example.canonical.Status;

public final class TransferResponse implements Serializable {

	private static final long serialVersionUID = -6573632342509110244L;

	public static TransferResponseBuilder create() {
		return TransferResponseBuilder.create();
	}
	
	private UUID transferIdentification;
	private Status statusWithdraw;
	private Status statusTransfer;
	
	public TransferResponse() {
		
	}

	protected TransferResponse(final UUID transferIdentification, final Status statusWithdraw, final Status statusTransfer) {
		this.transferIdentification = transferIdentification;
		this.statusWithdraw = statusWithdraw;
		this.statusTransfer = statusTransfer;
	}

	public UUID getTransferIdentification() {
		return transferIdentification;
	}

	public void setTransferIdentification(UUID transferIdentification) {
		this.transferIdentification = transferIdentification;
	}

	public Status getStatusWithdraw() {
		return statusWithdraw;
	}

	public void setStatusWithdraw(Status statusWithdraw) {
		this.statusWithdraw = statusWithdraw;
	}

	public Status getStatusTransfer() {
		return statusTransfer;
	}

	public void setStatusTransfer(Status statusTransfer) {
		this.statusTransfer = statusTransfer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((statusTransfer == null) ? 0 : statusTransfer.hashCode());
		result = prime * result + ((statusWithdraw == null) ? 0 : statusWithdraw.hashCode());
		result = prime * result + ((transferIdentification == null) ? 0 : transferIdentification.hashCode());
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
		return true;
	}

	@Override
	public String toString() {
		return "TransferResponse [transferIdentification=" + transferIdentification + ", statusWithdraw="
				+ statusWithdraw + ", statusTransfer=" + statusTransfer + "]";
	}
}
