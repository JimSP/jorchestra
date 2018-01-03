package br.com.jorchestra.example.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.jorchestra.example.canonical.Account;
import br.com.jorchestra.example.canonical.Status;

@Service
public class DepositIntoAccountExample {

	private static final Logger LOGGER = LoggerFactory.getLogger(DepositIntoAccountExample.class);

	public Status deposit(final Account account, final BigDecimal value) {
		LOGGER.debug("m=deposit, account=" + account + ", value=" + value);
		return Status.SUCCESS;
	}
}
