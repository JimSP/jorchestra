package br.com.jorchestra.example.util;

import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.jorchestra.example.canonical.Account;
import br.com.jorchestra.example.dto.TransferRequest;

public class CreateTransferRequest {

	@Test
	public void test() throws JsonProcessingException{
		final TransferRequest transferRequest = CreateTransferRequest.create();
		System.out.println(transferRequest);
		System.out.println(new ObjectMapper().writeValueAsString(transferRequest));
	}

	public static TransferRequest create() {
		final Account from = new Account(1L);
		final Account to = new Account(2L);
		final BigDecimal value = BigDecimal.valueOf(100);

		return TransferRequest //
				.create() //
				.withFrom(from) //
				.withTo(to) //
				.withValue(value) //
				.build();
	}
}
