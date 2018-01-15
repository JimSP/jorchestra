package org.springframework.context.annotation;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;

import br.com.jorchestra.util.JOrchestraDetectUseLocalPort;

@Component("jOrchestraMonitorInstance")
public class JOrchestraMonitorInstance {

	@Value("${slave.port:8081}")
	private String slavePort;

	@Value("${slave.main}")
	private String appMain;

	@Value("${slave.mode:true}")
	private String slaveMode;

	@Value("${master.port:8080}")
	private String masterPort;

	@Autowired
	private HazelcastInstance hazelcastInstance;

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

	@Scheduled(fixedDelay = 30000)
	public void monitor() {
		final ILock lock = hazelcastInstance.getLock("jOrchestraMonitorInstance.loadInstance");

		try {
			lock.lock();

			final Integer portSlave = Integer.parseInt(slavePort);
			final Integer portMaster = Integer.parseInt(masterPort);

			if (!Boolean.parseBoolean(slaveMode)) {// master load slave
				System.out.println("m=monitor, portSlave=" + portSlave);
				if (appMain != null && !JOrchestraDetectUseLocalPort.isInUse(portSlave)) {
					loadInstance(portSlave, Boolean.TRUE);
				} else if (appMain == null) {
					System.out.println("m=monitor, msg=\"slave.main not configuring application.properties.\"");
				} else {
					System.out.println("m=monitor, msg=\"portSlave=" + portSlave + " in use.\"");
				}
			} else {// slave load master
				if (!JOrchestraDetectUseLocalPort.isInUse(portMaster)) {
					System.out.println("m=monitor, portMaster=" + portMaster);
					loadInstance(portMaster, Boolean.FALSE);
				} else if (appMain == null) {
					System.out.println("m=monitor, msg=\"slave.main not configuring application.properties.\"");
				} else {
					System.out.println("m=monitor, msg=\"portMaster=" + portMaster + " in use.\"");
				}
			}
		} finally {
			lock.unlock();
		}
	}

	private void loadInstance(final Integer port, final Boolean slaveMode) {
		final Runtime runtime = Runtime.getRuntime();

		final String command = "java -jar " + appMain + " --server.port=" + port + " --slave.mode=" + slaveMode
				+ " --master.port=" + masterPort + " --slave.port=" + slavePort;
		System.out.println("m=loadInstance, command=" + command);

		try {
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
