package br.com.jorchestra.callable;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.jorchestra.util.JOrchestraContextUtils;

public class JOrchestraCallable implements Callable<Object>, Serializable {

	private static final long serialVersionUID = 6981532005948655731L;

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraCallable.class);

	private final String jOrchestraBeanName;
	private final String methodName;
	private final Class<?>[] parametersType;
	private final Object[] parameters;

	public JOrchestraCallable(final String jOrchestraBeanName, final String methodName, final Class<?>[] parametersType,
			final Object[] parameters) {
		this.jOrchestraBeanName = jOrchestraBeanName;
		this.methodName = methodName;
		this.parametersType = parametersType;
		this.parameters = parameters;
	}

	@Override
	public Object call() throws Exception {
		LOGGER.debug("m=call, parameters=" + Arrays.toString(parameters));

		try {
			final Object jOrchestraBean = JOrchestraContextUtils.getJorchestraBean(jOrchestraBeanName);

			return JOrchestraContextUtils.getMethosByJOrchestraPath(jOrchestraBean, methodName, parametersType)
					.invoke(jOrchestraBean, parameters);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("m=call, parameters=" + Arrays.toString(parameters), e);
			throw new RuntimeException("m=call, parameters=" + Arrays.toString(parameters), e);
		}
	}

}