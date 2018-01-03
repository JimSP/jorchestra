package br.com.jorchestra.listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

public class JOrchestraEventListener implements MessageListener<Object[]> {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraEventListener.class);

	private final Object jOrchestraBean;
	private final Method method;
	private final ITopic<Object> resultTopic;

	public JOrchestraEventListener(final Object jOrchestraBean, final Method method, final ITopic<Object> resultTopic) {
		this.jOrchestraBean = jOrchestraBean;
		this.method = method;
		this.resultTopic = resultTopic;
	}

	@Override
	public void onMessage(final Message<Object[]> message) {
		LOGGER.debug("m=onMessage, parameters=" + Arrays.toString(message.getMessageObject()));

		Object result = null;

		final Object[] parameters = message.getMessageObject();
		try {
			result = method.invoke(jOrchestraBean, parameters);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("m=onMessage, parameters=" + Arrays.toString(message.getMessageObject()), e);
			result = e;
		} finally {
			resultTopic.publish(result);
		}
	}
}
