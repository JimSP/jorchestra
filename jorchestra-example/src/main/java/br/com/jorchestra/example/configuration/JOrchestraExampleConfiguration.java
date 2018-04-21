package br.com.jorchestra.example.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.JOrchestraMonitorInstance;

import com.hazelcast.core.HazelcastInstance;

import br.com.jorchestra.service.JOrchestraRegisterSystemEvents;
import br.com.jorchestra.util.JOrchestraContextUtils;

@Configuration
public class JOrchestraExampleConfiguration {

	@Bean("hazelcastInstance")
	public HazelcastInstance hazelcastInstance() {
		return JOrchestraContextUtils.getJOrchestraHazelcastInstance();
	}
	
	@Bean("jOrchestraRegisterSystemEvents")
	public JOrchestraRegisterSystemEvents JOrchestraRegisterSystemEvents() {
		return new JOrchestraRegisterSystemEvents();
	}
	
	@Bean("jOrchestraMonitorInstance")
	public JOrchestraMonitorInstance jOrchestraMonitorInstance() {
		return new JOrchestraMonitorInstance();
	}
}
