package br.com.jorchestra.util;

import java.io.IOException;
import java.net.ServerSocket;

public class JOrchestraDetectUseLocalPort {
	
	private JOrchestraDetectUseLocalPort() {
		
	}

	public static Boolean isInUse(final Integer port) {
		try(final ServerSocket serverSocket = new ServerSocket(port)){
			return Boolean.FALSE;
		}catch (IOException e) {
			return Boolean.TRUE;
		}
	}
	
	public static int incrementPortIfIsInUser(final Integer port) {
		int targetPort = port;
		while (JOrchestraDetectUseLocalPort.isInUse(targetPort)) {
			targetPort++;
		}
		return targetPort;
	}
}
