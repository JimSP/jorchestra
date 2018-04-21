package org.springframework.context.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hazelcast.HazelcastKeyValueAdapter;
import org.springframework.data.hazelcast.repository.config.EnableHazelcastRepositories;
import org.springframework.data.keyvalue.core.KeyValueOperations;
import org.springframework.data.keyvalue.core.KeyValueTemplate;

import com.hazelcast.core.HazelcastInstance;

@Configuration
@EnableHazelcastRepositories
@DependsOn("jOrchestraAutoConfiguration")
public class JOrchestraRepositoryAutoConfiguration {

	@Autowired
	private HazelcastInstance hazelcastInstance;

	@Bean
	public KeyValueOperations keyValueTemplate() {
		return new KeyValueTemplate(new HazelcastKeyValueAdapter(hazelcastInstance));
	}

	@Bean
	public HazelcastKeyValueAdapter hazelcastKeyValueAdapter(final HazelcastInstance hazelcastInstance) {
		return new HazelcastKeyValueAdapter(hazelcastInstance);
	}

}
