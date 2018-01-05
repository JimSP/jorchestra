package br.com.jorchestra.configuration;

import java.util.Collections;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jOcrhestra")
public class JOrchestraConfigurationProperties {

	private String name;
	private String test;
	private String clusterName;
	private String allowedOrigins;
	private Integer poolSize;
	private Map<String, String> eventsClassMap;

	public String getName() {
		return name == null ? "JOrquestra-DEV" : name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTest() {
		return test == null ? "jOcrhestra-success" : test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getClusterName() {
		return clusterName == null ? "jOcrhestra" : clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getAllowedOrigins() {
		return allowedOrigins == null ? "*" : allowedOrigins;
	}

	public void setAllowedOrigins(String allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}

	public Integer getPoolSize() {
		return poolSize == null ? 5 : poolSize;
	}

	public void setPoolSize(Integer poolSize) {
		this.poolSize = poolSize;
	}

	public Map<String, String> getEventsClassMap() {
		return eventsClassMap == null ? Collections.emptyMap() : eventsClassMap;
	}

	public void setEventsClassMap(Map<String, String> getEventsClassMap) {
		this.eventsClassMap = getEventsClassMap;
	}

	@Override
	public String toString() {
		return "JOrchestraConfigurationProperties [name=" + getName() + ", test=" + getTest() + ", clusterName="
				+ getClusterName() + ", allowedOrigins=" + getAllowedOrigins() + ", poolSize=" + getPoolSize()
				+ ", eventsClassMap=" + getEventsClassMap() + "]";
	}

}
