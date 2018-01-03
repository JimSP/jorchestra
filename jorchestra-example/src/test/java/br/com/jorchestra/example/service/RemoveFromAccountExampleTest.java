package br.com.jorchestra.example.service;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.jorchestra.example.canonical.Account;
import br.com.jorchestra.example.canonical.Status;
import br.com.jorchestra.example.service.RemoveFromAccountExample;

@RunWith(MockitoJUnitRunner.class)
public class RemoveFromAccountExampleTest {

	@InjectMocks
	private RemoveFromAccountExample removeFromAccountExample;

	@Test
	public void depositTest() {

		final Account account = new Account(1L);
		final BigDecimal value = BigDecimal.valueOf(100);

		final Status status = removeFromAccountExample.toWithdraw(account, value);

		Assert.assertEquals(Status.SUCCESS, status);
	}

}
