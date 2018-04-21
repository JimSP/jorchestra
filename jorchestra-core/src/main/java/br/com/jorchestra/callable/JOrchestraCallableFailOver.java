package br.com.jorchestra.callable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import br.com.jorchestra.canonical.JOrchestraHandle;
import br.com.jorchestra.util.JOrchestraContextUtils;

public class JOrchestraCallableFailOver implements Callable<Object>, Serializable {

	private static final long serialVersionUID = -7819806469279625683L;

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraCallableFailOver.class);

	private final JOrchestraHandle jOrchestraHandle;
	private final Class<?>[] parametersType;
	private final Object[] parameters;

	public JOrchestraCallableFailOver(final JOrchestraHandle jOrchestraHandle, final Class<?>[] parametersType,
			final Object[] parameters) {
		Assert.hasText(jOrchestraHandle.getFailOverMethodName(), "failOverMethodName not has a valid text.");
		
		this.jOrchestraHandle = jOrchestraHandle;
		this.parametersType = parametersType;
		this.parameters = parameters;
	}

	@Override
	public Object call() throws Exception {
		LOGGER.debug("m=call, parameters=" + Arrays.toString(parameters));

		final Object jOrchestraBean = JOrchestraContextUtils
				.getJorchestraBean(jOrchestraHandle.getjOrchestraBeanName());

		return JOrchestraContextUtils
				.getMethosByJOrchestraPath(jOrchestraBean, jOrchestraHandle.getFailOverMethodName(), parametersType)
				.invoke(jOrchestraBean, parameters);
	}
}
