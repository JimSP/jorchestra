package br.com.jorchestra.example.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.jorchestra.example.canonical.Account;
import br.com.jorchestra.example.canonical.Status;
import br.com.jorchestra.example.dto.TransferRequest;
import br.com.jorchestra.example.dto.TransferResponse;
import br.com.jorchestra.example.dto.TransferResponseBuilder;
import br.com.jorchestra.example.notification.JOrchestraNotificationEletronicTransferAccount;
import br.com.jorchestra.example.util.CreateTransferRequest;

@RunWith(MockitoJUnitRunner.class)
public class ElectronicTransferOfFundsExampleTest {

	@InjectMocks
	private ElectronicTransferOfFundsExample electronicTransferOfFundsExample;

	@Mock
	private RemoveFromAccountExample removeFromAccount;

	@Mock
	private DepositIntoAccountExample depositIntoAccount;

	@Mock
	private JOrchestraNotificationEletronicTransferAccount jOrchestraNotificationEletronicTransferAccount;

	private final TransferRequest transferRequest = CreateTransferRequest.create();
	private final UUID transferIdentification = transferRequest.getTransferIdentificationUUID();

	final Account from = new Account(1L);
	final Account to = new Account(2L);
	final BigDecimal value = BigDecimal.valueOf(100);

	@Test
	public void statusSuccessTest() {
		final Status statusWithdraw = Status.SUCCESS;
		final Status statusTransfer = Status.SUCCESS;
		final Status notificationStatus = Status.SUCCESS;

		transferTest(statusWithdraw, statusTransfer, notificationStatus);
	}

	@Test
	public void statusErrorTest() {
		final Status statusWithdraw = Status.SUCCESS;
		final Status statusTransfer = Status.ERROR;
		final Status notificationStatus = Status.SUCCESS;

		transferTest(statusWithdraw, statusTransfer, notificationStatus);
	}

	@Test
	public void statusWithdrawErrorTest() {
		final Status statusWithdraw = Status.ERROR;
		final Status statusTransfer = Status.ERROR;
		final Status notificationStatus = Status.SUCCESS;

		transferTest(statusWithdraw, statusTransfer, notificationStatus);
	}

	@Test
	public void statusWithdrawErrorAndTransferSuccessTest() {
		final Status statusWithdraw = Status.ERROR;
		final Status statusTransfer = Status.SUCCESS;
		final Status notificationStatus = Status.SUCCESS;

		transferTest(statusWithdraw, statusTransfer, notificationStatus);
	}

	private void transferTest(final Status statusWithdraw, final Status statusTransfer,
			final Status notificationStatus) {

		final TransferResponse expectedTransferResponse = TransferResponseBuilder.create()
				.withStatusWithdraw(statusWithdraw) //
				.withStatusTransfer(statusWithdraw == Status.SUCCESS ? statusTransfer : Status.SUCCESS) //
				.withTransferIdentificationUUID(transferIdentification) //
				.withTransferRequest(transferRequest) //
				.build();

		Mockito.when(removeFromAccount.toWithdraw(from, value)).thenReturn(statusWithdraw);
		Mockito.when(jOrchestraNotificationEletronicTransferAccount.account(expectedTransferResponse))
				.thenReturn(notificationStatus);

		final Boolean statusTransferExpected = Status.isSuccess(statusWithdraw);

		if (statusTransferExpected) {
			Mockito.when(depositIntoAccount.deposit(to, value)).thenReturn(statusTransfer);
		} else {
			Mockito.when(depositIntoAccount.deposit(from, value)).thenReturn(Status.SUCCESS);
		}

		final TransferResponse transferResponse = electronicTransferOfFundsExample.transfer(transferRequest);

		Assert.assertEquals(expectedTransferResponse, transferResponse);
	}
}
