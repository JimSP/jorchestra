package br.com.jorchestra.example.client;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import br.com.jorchestra.example.JorchestraApplication;
import br.com.jorchestra.example.util.JOrchestraWebSocketClientHandle;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { JorchestraApplication.class })
public class JOrchestraClientExample {

	private static final String URL = "ws://localhost:8080/account-transfer";

	private static WebSocketConnectionManager webSocketConnectionManager() {
		final WebSocketConnectionManager webSocketConnectionManager = new WebSocketConnectionManager(
				standardWebSocketClient(), jOrchestraWebSocketClientHandle, URL);
		webSocketConnectionManager.setOrigin("*");
		return webSocketConnectionManager;
	}

	private static StandardWebSocketClient standardWebSocketClient() {
		return new StandardWebSocketClient();
	}

	private static JOrchestraWebSocketClientHandle jOrchestraWebSocketClientHandle = new JOrchestraWebSocketClientHandle();

	private WebSocketConnectionManager webSocketConnectionManager = webSocketConnectionManager();

	@Before
	public void before() {
		webSocketConnectionManager.start();
	}
	
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
	
	@After
	public void after() {
		webSocketConnectionManager.stop();
	}

}
