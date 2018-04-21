package br.com.jorchestra.example.service;

import br.com.jorchestra.annotation.JOrchestra;
import br.com.jorchestra.annotation.JOrchestraFailover;

@JOrchestra(path = "jOrchestra")
public class TrustService {

	@JOrchestraFailover(failOverMethodName = "failover")
	public String trust() {
		throw new RuntimeException();
	}

	public String failover() {
		return "Hi :-) JOrchestra is cool!";
	}
}
