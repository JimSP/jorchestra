package br.com.jorchestra.example.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableJOrchestra;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
import br.com.jorchestra.service.JOrchestraRegisterSystemEvents;

@Configuration
@EnableJOrchestra
public class JOrchestraExampleConfiguration {

	@Autowired
	private JOrchestraConfigurationProperties jorchestraConfigurationProperties;

	@Bean("hazelcastInstance")
	public HazelcastInstance hazelcastInstance() {
		return Hazelcast.getOrCreateHazelcastInstance(new Config(jorchestraConfigurationProperties.getClusterName()));
	}
	
	@Bean("jOrchestraRegisterSystemEvents")
	public JOrchestraRegisterSystemEvents JOrchestraRegisterSystemEvents() {
		return new JOrchestraRegisterSystemEvents();
	}

}
