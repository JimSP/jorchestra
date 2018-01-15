package org.springframework.context.annotation;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.web.WebFilter;

import br.com.jorchestra.configuration.JOrchestraConfigurationProperties; 

@Configuration
public class JOrchestraSharedSessionConfiguration {
	
	@Autowired
	private JOrchestraConfigurationProperties jOrchestraConfigurationProperties;

	@Bean("hazelcastInstance")
	public HazelcastInstance hazelcastInstance() {
		return Hazelcast.getOrCreateHazelcastInstance(new Config(jOrchestraConfigurationProperties.getClusterName()));
	}

	@Bean
	public WebFilter webFilter(HazelcastInstance hazelcastInstance) {
		final Properties properties = new Properties();
		properties.put("instance-name", hazelcastInstance.getName());
		properties.put("sticky-session", "false");
		final WebFilter webFilter = new WebFilter(properties);
		return webFilter;
	}
}
