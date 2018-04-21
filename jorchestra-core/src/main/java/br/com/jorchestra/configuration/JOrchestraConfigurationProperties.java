package br.com.jorchestra.configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jOcrhestra")
public class JOrchestraConfigurationProperties {

	private String name;
	private String test;
	private String clusterName;
	private String allowedOrigins;
	private Integer poolSize;
	private Long poolingMilliseconds;
	private Map<String, List<String>> eventsClassMap;
	private Boolean supportsPartialMessages;
	private String username;
	private String password;
	private Boolean enableTcpLink;
	private List<String> tcpClusterMembers;
	private Boolean managementCenterConfigEnable;
	private Integer managementCenterConfigUpdateInterval;
	private String managementCenterConfigUrl;
	private Boolean portServerAutoIncrement;

	public String getName() {
		return name == null ? "JOrchestra-DEV" : name;
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
	
	public Long getPoolingMilliseconds() {
		return poolingMilliseconds == null ? 1000L : poolingMilliseconds;
	}
	
	public void setPoolingMilliseconds(final Long poolingMilliseconds) {
		this.poolingMilliseconds = poolingMilliseconds;
	}

	public Map<String, List<String>> getEventsClassMap() {
		return eventsClassMap == null ? Collections.emptyMap() : eventsClassMap;
	}

	public void setEventsClassMap(Map<String, List<String>> getEventsClassMap) {
		this.eventsClassMap = getEventsClassMap;
	}

	public Boolean getSupportsPartialMessages() {
		return supportsPartialMessages == null ? false : supportsPartialMessages;
	}

	public void setSupportsPartialMessages(Boolean supportsPartialMessages) {
		this.supportsPartialMessages = supportsPartialMessages;
	}

	public String getUsername() {
		return username == null ? "JOrchestra" : username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password == null ? "JOrchestra" : password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnableTcpLink() {
		return enableTcpLink == null ? Boolean.FALSE : enableTcpLink;
	}

	public void setEnableTcpLink(Boolean enableTcpLink) {
		this.enableTcpLink = enableTcpLink;
	}

	public List<String> getTcpClusterMembers() {
		return tcpClusterMembers == null ? Collections.emptyList() : tcpClusterMembers;
	}

	public void setTcpClusterMembers(String tcpClusterMembers) {
		if (tcpClusterMembers != null && !tcpClusterMembers.equalsIgnoreCase("")) {
			this.tcpClusterMembers = Arrays.asList(tcpClusterMembers.split("[,]"));
		}
	}

	public Boolean getManagementCenterConfigEnable() {
		return managementCenterConfigEnable == null ? Boolean.FALSE : managementCenterConfigEnable;
	}

	public void setManagementCenterConfigEnable(Boolean managementCenterConfigEnable) {
		this.managementCenterConfigEnable = managementCenterConfigEnable;
	}

	public Integer getManagementCenterConfigUpdateInterval() {
		return managementCenterConfigUpdateInterval == null ? 60000 : managementCenterConfigUpdateInterval;
	}

	public void setManagementCenterConfigUpdateInterval(Integer managementCenterConfigUpdateInterval) {
		this.managementCenterConfigUpdateInterval = managementCenterConfigUpdateInterval;
	}

	public String getManagementCenterConfigUrl() {
		return managementCenterConfigUrl == null ? "http://localhost:9090/admin" : managementCenterConfigUrl;
	}

	public void setManagementCenterConfigUrl(String managementCenterConfigUrl) {
		this.managementCenterConfigUrl = managementCenterConfigUrl;
	}
	
	public Boolean getPortServerAutoIncrement() {
		return portServerAutoIncrement == null ? Boolean.FALSE : portServerAutoIncrement;
	}

	public void setPortServerAutoIncrement(Boolean portServerAutoIncrement) {
		this.portServerAutoIncrement = portServerAutoIncrement;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allowedOrigins == null) ? 0 : allowedOrigins.hashCode());
		result = prime * result + ((clusterName == null) ? 0 : clusterName.hashCode());
		result = prime * result + ((eventsClassMap == null) ? 0 : eventsClassMap.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((poolSize == null) ? 0 : poolSize.hashCode());
		result = prime * result + ((supportsPartialMessages == null) ? 0 : supportsPartialMessages.hashCode());
		result = prime * result + ((test == null) ? 0 : test.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JOrchestraConfigurationProperties other = (JOrchestraConfigurationProperties) obj;
		if (allowedOrigins == null) {
			if (other.allowedOrigins != null)
				return false;
		} else if (!allowedOrigins.equals(other.allowedOrigins))
			return false;
		if (clusterName == null) {
			if (other.clusterName != null)
				return false;
		} else if (!clusterName.equals(other.clusterName))
			return false;
		if (eventsClassMap == null) {
			if (other.eventsClassMap != null)
				return false;
		} else if (!eventsClassMap.equals(other.eventsClassMap))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (poolSize == null) {
			if (other.poolSize != null)
				return false;
		} else if (!poolSize.equals(other.poolSize))
			return false;
		if (supportsPartialMessages == null) {
			if (other.supportsPartialMessages != null)
				return false;
		} else if (!supportsPartialMessages.equals(other.supportsPartialMessages))
			return false;
		if (test == null) {
			if (other.test != null)
				return false;
		} else if (!test.equals(other.test))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		return "JOrchestraConfigurationProperties [name=" + getName() + ", test=" + getTest() + ", clusterName="
				+ getClusterName() + ", allowedOrigins=" + getAllowedOrigins() + ", poolSize=" + getPoolSize()
				+ ", eventsClassMap=" + toString(getEventsClassMap().entrySet(), maxLen) + ", supportsPartialMessages="
				+ getSupportsPartialMessages() + ", enableTcpLink=" + getEnableTcpLink() + ", tcpClusterMembers="
				+ toString(getTcpClusterMembers(), maxLen) + "]";
	}

	private String toString(final Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}
}
