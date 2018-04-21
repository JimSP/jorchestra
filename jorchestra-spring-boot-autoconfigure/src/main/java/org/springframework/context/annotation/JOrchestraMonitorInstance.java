package org.springframework.context.annotation;

import java.io.IOException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ISet;

import br.com.jorchestra.canonical.JOrchestraHandle;
import br.com.jorchestra.util.JOrchestraContextUtils;
import br.com.jorchestra.util.JOrchestraDetectUseLocalPort;

@Component("jOrchestraMonitorInstance")
public class JOrchestraMonitorInstance {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraMonitorInstance.class);

	@Value("${slave.port:8081}")
	private String slavePort;

	@Value("${slave.main}")
	private String appMain;

	@Value("${slave.mode:true}")
	private String slaveMode;

	@Value("${master.port:8080}")
	private String masterPort;

	public String getSlavePort() {
		return slavePort;
	}

	public String getAppMain() {
		return appMain;
	}

	public String getSlaveMode() {
		return slaveMode;
	}

	public String getMasterPort() {
		return masterPort;
	}

	@Scheduled(fixedDelay = 1000)
	public void monitor() {
		final HazelcastInstance hazelcastInstance = JOrchestraContextUtils.getJOrchestraHazelcastInstance();
		final ISet<JOrchestraHandle> jOrchestraPathRegisterSet = hazelcastInstance.getSet("jOrchestraPathRegisterSet");
		final Integer portSlave = Integer.parseInt(slavePort);
		final Integer portMaster = Integer.parseInt(masterPort);

		if (!Boolean.parseBoolean(slaveMode)) {// master load slave
			verifyPortAndLoadInstance(portSlave, jOrchestraPathRegisterSet, Boolean.TRUE);
		} else {// slave load master
			verifyPortAndLoadInstance(portMaster, jOrchestraPathRegisterSet, Boolean.FALSE);
		}
	}

	private void verifyPortAndLoadInstance(final Integer port, final ISet<JOrchestraHandle> jOrchestraPathRegisterSet, final Boolean slaveMode) {
		if (appMain != null && !JOrchestraDetectUseLocalPort.isInUse(port)) {

			final Iterator<JOrchestraHandle> handleIterator = jOrchestraPathRegisterSet.iterator();
			while (handleIterator.hasNext()) {
				final JOrchestraHandle jOrchestraHandle = handleIterator.next();
				if (jOrchestraHandle.getPort() == port.intValue()) {
					jOrchestraPathRegisterSet.remove(jOrchestraHandle);
				}
			}
			
			loadInstance(port, slaveMode);
			while (!JOrchestraDetectUseLocalPort.isInUse(port))
				;
			
		} else if (appMain == null) {
			LOGGER.warn("m=verifyPortAndLoadInstance, msg=\"slave.main not configuring application.properties.\"");
		} else {
			LOGGER.debug("m=verifyPortAndLoadInstance, msg=\"port=" + port + " in use.\"");
		}
	}

	private void loadInstance(final Integer port, final Boolean slaveMode) {
		final Runtime runtime = Runtime.getRuntime();

		final String command = "java -jar " + appMain + " --server.port=" + port + " --slave.mode=" + slaveMode
				+ " --master.port=" + masterPort + " --slave.port=" + slavePort;
		LOGGER.info("m=loadInstance, command=" + command);

		try {
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
