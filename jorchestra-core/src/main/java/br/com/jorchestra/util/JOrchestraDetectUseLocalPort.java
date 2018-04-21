package br.com.jorchestra.util;

import java.io.IOException;
import java.net.Socket;

public class JOrchestraDetectUseLocalPort {
	
	private JOrchestraDetectUseLocalPort() {
		
	}

	public static Boolean isInUse(final Integer port) {
		try(final Socket socket= new Socket("127.0.0.1", port)){
			return socket.isConnected();
		}catch (IOException e) {
			return Boolean.FALSE;
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
