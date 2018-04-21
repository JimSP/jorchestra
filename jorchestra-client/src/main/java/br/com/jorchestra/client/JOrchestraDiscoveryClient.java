package br.com.jorchestra.client;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.util.function.Consumer;

import br.com.jorchestra.canonical.DiscroveryEventType;
import br.com.jorchestra.canonical.JOrchestraHandle;

@ClientEndpoint
public class JOrchestraDiscoveryClient {

	private final Class<? extends Object[]> safeType = new Object[] { DiscroveryEventType.class,
			JOrchestraHandle.class }.getClass();

	private final Map<DiscroveryEventType, Consumer<JOrchestraHandle>> map = Collections
			.synchronizedMap(new HashMap<>());

	private JOrchestraDiscoveryClient() {
		map.put(DiscroveryEventType.ADD, (jOrchestraHandle) -> JOrchestraDiscoveryRegister.Singleton
				.getJOrchestraRegisterInstance().add(jOrchestraHandle));

		map.put(DiscroveryEventType.REMOVE, (jOrchestraHandle) -> JOrchestraDiscoveryRegister.Singleton
				.getJOrchestraRegisterInstance().remove(jOrchestraHandle));
	}

	@OnMessage
	public void onMessage(final String message) throws JsonParseException, JsonMappingException, IOException {
		final ObjectMapper objectMapper = new ObjectMapper();
		final Object[] result = objectMapper.readValue(message, safeType);

		int i = 0;
		final DiscroveryEventType discroveryEventType = (DiscroveryEventType) result[i++];
		final JOrchestraHandle jOrchestraHandle = (JOrchestraHandle) result[i++];

		Optional //
				.ofNullable(map.get(discroveryEventType)) //
				.ifPresent(consumer -> consumer.accept(jOrchestraHandle));

	}

	@OnError
	public void onError(final Session session, final Throwable t) {

	}

	protected static JOrchestraDiscoveryClient create() {
		return new JOrchestraDiscoveryClient();
	}
}
