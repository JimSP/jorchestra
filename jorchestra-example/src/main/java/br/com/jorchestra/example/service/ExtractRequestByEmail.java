package br.com.jorchestra.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.jorchestra.annotation.JOrchestra;
import br.com.jorchestra.canonical.JOrchestraSignal;
import br.com.jorchestra.example.dto.ExtractRequest;

@JOrchestra(path = "extractByEmail", jOrchestraSignalType = JOrchestraSignal.PUBLISH)
public class ExtractRequestByEmail {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExtractRequestByEmail.class);

	public void send(final ExtractRequest extractRequest) {
		LOGGER.debug("m=send, extractRequest=" + extractRequest);
		
		//compoem extrato
		
		//envia por email
	}
}
