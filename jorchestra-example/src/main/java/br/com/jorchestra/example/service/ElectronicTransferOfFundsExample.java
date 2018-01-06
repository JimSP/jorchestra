package br.com.jorchestra.example.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.com.jorchestra.annotation.JOrchestra;
import br.com.jorchestra.example.canonical.Account;
import br.com.jorchestra.example.canonical.Status;
import br.com.jorchestra.example.dto.TransferRequest;
import br.com.jorchestra.example.dto.TransferResponse;
import br.com.jorchestra.example.notification.JOrchestraNotificationEletronicTransferAccount;

@JOrchestra(path = "account")
public class ElectronicTransferOfFundsExample {

	private static final Logger LOGGER = LoggerFactory.getLogger(ElectronicTransferOfFundsExample.class);

	@Autowired
	private RemoveFromAccountExample removeFromAccount;

	@Autowired
	private DepositIntoAccountExample depositIntoAccount;

	@Autowired
	private JOrchestraNotificationEletronicTransferAccount jOrchestraNotificationEletronicTransferAccount;

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

		final TransferResponse transferResponse = TransferResponse.create() //
				.withStatusWithdraw(statusWithdraw) //
				.withStatusTransfer(statusTransfer) //
				.setTransferIdentification(transferRequest.getTransferIdentification()) //
				.withTransferRequest(transferRequest) //
				.build();

		final Status statusNotification = jOrchestraNotificationEletronicTransferAccount.account(transferResponse);

		//poderia persistir async em um SGDB...
		log(transferResponse, statusNotification);

		return transferResponse;
	}

	// @Async
	// @Transaction
	private void log(final TransferResponse transferResponse, final Status statusNotification) {
		LOGGER.info("m=persist, transferResponse=" + transferResponse, ", statusNotification=" + statusNotification);
	}
}
