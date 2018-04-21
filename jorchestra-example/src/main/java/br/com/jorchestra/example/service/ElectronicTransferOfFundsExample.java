package br.com.jorchestra.example.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import br.com.jorchestra.annotation.JOrchestra;
import br.com.jorchestra.example.canonical.Account;
import br.com.jorchestra.example.canonical.Status;
import br.com.jorchestra.example.dto.TransferRequest;
import br.com.jorchestra.example.dto.TransferResponse;
import br.com.jorchestra.example.dto.TransferResponseBuilder;
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

		final TransferResponseBuilder transferResponseBuilder = TransferResponse.create();
		
		if (Status.isSuccess(statusWithdraw)) {
			final Status statusTransfer = depositIntoAccount.deposit(to, value);
			transferResponseBuilder.withStatusTransfer(statusTransfer);
		} else {
			final Status statusTransfer = depositIntoAccount.deposit(from, value);
			transferResponseBuilder.withStatusTransfer(statusTransfer);
		}

		final TransferResponse transferResponse = transferResponseBuilder//
				.setTransferIdentification(transferRequest.getTransferIdentification()) //
				.withTransferRequest(transferRequest) //
				.withStatusWithdraw(statusWithdraw)
				.build();

		final Status statusNotification = jOrchestraNotificationEletronicTransferAccount.account(transferResponse);

		//poderia persistir async em um SGDB...
		log(transferResponse, statusNotification);

		return transferResponse;
	}

	@Async
	private void log(final TransferResponse transferResponse, final Status statusNotification) {
		LOGGER.info("m=persist, transferResponse=" + transferResponse, ", statusNotification=" + statusNotification);
	}
}
