package br.com.jorchestra.example.client;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableJOrchestra;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import br.com.jorchestra.example.util.JOrchestraWebSocketClientHandle;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { JOrchestraClientExample.JOrchestraConfigurationExample.class })
@EnableJOrchestra
@Ignore
public class JOrchestraClientExample {

	private static JOrchestraWebSocketClientHandle jOrchestraWebSocketClientHandle = new JOrchestraWebSocketClientHandle();

	@Autowired
	private WebSocketConnectionManager webSocketConnectionManager;

	@Test
	public void isRunning() {
		Assert.assertTrue(webSocketConnectionManager.isRunning());
	}

	@Test
	public void isAfterConnectionEstablished() {
		Assert.assertTrue(jOrchestraWebSocketClientHandle.isAfterConnectionEstablished());
	}

	@Test
	public void isHandleTextMessage() {
		Assert.assertTrue(jOrchestraWebSocketClientHandle.isHandleTextMessage());
	}

	@Test
	public void isAfterConnectionClosed() {
		Assert.assertTrue(jOrchestraWebSocketClientHandle.isHandleTextMessage());
	}

	@Test
	public void isHandleTransportError() {
		Assert.assertTrue(jOrchestraWebSocketClientHandle.isHandleTransportError());
	}

	//@Configuration
	public static class JOrchestraConfigurationExample {

		private static final String URL = "ws://localhost:8080/account-transfer";

		@Bean(destroyMethod = "stop")
		public WebSocketConnectionManager connectionManager() {
			final WebSocketConnectionManager webSocketConnectionManager = new WebSocketConnectionManager(
					standardWebSocketClient(), jOrchestraWebSocketClientHandle, URL);
			webSocketConnectionManager.setOrigin("*");
			webSocketConnectionManager.setAutoStartup(true);
			return webSocketConnectionManager;
		}

		public StandardWebSocketClient standardWebSocketClient() {
			return new StandardWebSocketClient();
		}

	}
}
