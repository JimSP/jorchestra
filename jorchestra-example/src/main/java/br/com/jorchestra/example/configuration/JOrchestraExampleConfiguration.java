package br.com.jorchestra.example.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableJOrchestra;

import br.com.jorchestra.service.JOrchestraBeans;

@Configuration
@EnableJOrchestra
public class JOrchestraExampleConfiguration {

	@Bean
	public JOrchestraBeans JOrchestraBeans() {
		return new JOrchestraBeans();
	}
}
