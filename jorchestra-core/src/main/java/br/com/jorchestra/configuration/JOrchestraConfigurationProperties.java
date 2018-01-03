package br.com.jorchestra.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jocrhestra")
public class JOrchestraConfigurationProperties {

	private String test;
	private String instanceName;
	private String allowedOrigins;
	private Integer poolSize;

	public String getTest() {
		return test == null ? "jorchestra-success" : test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getInstanceName() {
		return instanceName == null ? "jorchestra" : instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
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

	@Override
	public String toString() {
		return "JOrchestraConfigurationProperties [test=" + getTest() + ", instanceName=" + getInstanceName()
				+ ", allowedOrigins=" + getAllowedOrigins() + ", poolSize=" + getPoolSize() + "]";
	}
}
