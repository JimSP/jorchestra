package br.com.jorchestra.example.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.jorchestra.example.canonical.Account;
import br.com.jorchestra.example.canonical.Status;

@Service
public class RemoveFromAccountExample {

	private static final Logger LOGGER = LoggerFactory.getLogger(RemoveFromAccountExample.class);

	public Status toWithdraw(final Account account, final BigDecimal value) {
		LOGGER.debug("m=toWithdraw, account=" + account + ", value=" + value);
		return Status.SUCCESS;
	}
}
