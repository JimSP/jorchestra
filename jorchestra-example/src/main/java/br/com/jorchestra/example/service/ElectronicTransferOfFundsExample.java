package br.com.jorchestra.example.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.jorchestra.annotation.JOrchestra;
import br.com.jorchestra.example.canonical.Account;
import br.com.jorchestra.example.canonical.Status;
import br.com.jorchestra.example.dto.TransferRequest;
import br.com.jorchestra.example.dto.TransferResponse;

@JOrchestra(path = "account")
public class ElectronicTransferOfFundsExample {

	private static final Logger LOGGER = LoggerFactory.getLogger(ElectronicTransferOfFundsExample.class);

	@Autowired
	private RemoveFromAccountExample removeFromAccount;

	@Autowired
	private DepositIntoAccountExample depositIntoAccount;

	public TransferResponse transfer(final TransferRequest transferRequest) {

		LOGGER.debug("m=transfer, transferRequest=" + transferRequest);

		final Account from = transferRequest.getFrom();
		final Account to = transferRequest.getTo();
		final BigDecimal value = transferRequest.getValue();

		final Status statusWithdraw = removeFromAccount.toWithdraw(from, value);

		Status statusTransfer = null;
		if (Status.isSuccess(statusWithdraw)) {
			statusTransfer = depositIntoAccount.deposit(to, value);
		} else {
			statusTransfer = depositIntoAccount.deposit(from, value);
		}

		return TransferResponse //
				.create() //
				.withStatusWithdraw(statusWithdraw) //
				.withStatusTransfer(statusTransfer) //
				.withTransferIdentification(transferRequest.getTransferIdentification()) //
				.build();
	}
}
