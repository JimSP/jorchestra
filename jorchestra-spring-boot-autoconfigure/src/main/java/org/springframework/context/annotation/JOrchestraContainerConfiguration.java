package org.springframework.context.annotation;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
import br.com.jorchestra.util.JOrchestraContextUtils;
import br.com.jorchestra.util.JOrchestraDetectUseLocalPort;

@Configuration
@EnableConfigurationProperties(JOrchestraConfigurationProperties.class)
public class JOrchestraContainerConfiguration {

	@Autowired
	private JOrchestraConfigurationProperties jOrchestraConfigurationProperties;

	@Value("${server.port:8080}")
	private Integer serverPort;

	@Bean
	public EmbeddedServletContainerCustomizer embeddedServletContainerCustomizer() {
		return (container -> {
			if (jOrchestraConfigurationProperties.getPortServerAutoIncrement()) {
				final int targetPort = JOrchestraDetectUseLocalPort.incrementPortIfIsInUser(serverPort);
				container.setPort(targetPort);
				registerPort(targetPort);
			} else {
				container.setPort(serverPort);
				registerPort(serverPort);
			}
		});
	}

	private void registerPort(final int targetPort) {
		try {
			JOrchestraContextUtils.registerAddressAndPort(Inet4Address.getLocalHost().getHostAddress(), targetPort);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

}
