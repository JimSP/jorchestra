package br.com.jorchestra.example.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.jorchestra.canonical.JOrchestraCommand;
import br.com.jorchestra.dto.JOrchestraAdminRequest;
import br.com.jorchestra.example.canonical.Account;
import br.com.jorchestra.example.dto.TransferRequest;

public class CreateTransferRequest {

	@Test
	public void test() throws JsonProcessingException {
		final TransferRequest transferRequest = CreateTransferRequest.create();
		System.out.println(new ObjectMapper().writeValueAsString(transferRequest));
		System.out.println(new ObjectMapper().writeValueAsString("oi"));
	}

	@Test
	public void teste1() throws JsonProcessingException {
		final JOrchestraCommand jOrchestraCommand = JOrchestraCommand.SHELL;
		final String jorchestaPath = null;
		final String sessionId = null;
		final String requestId = null;
		final String username = "JOrchestra";
		final String password = "JOrchestra";
		final Map<String, String> extraData = new HashMap<>();
		extraData.put(JOrchestraCommand.SHEL_COMMAND, "ls -l");
		
		final JOrchestraAdminRequest jOrchestraAdminRequest = new JOrchestraAdminRequest(jOrchestraCommand, jorchestaPath, sessionId, requestId, username, password, extraData);
		System.out.println(new ObjectMapper().writeValueAsString(jOrchestraAdminRequest));
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
