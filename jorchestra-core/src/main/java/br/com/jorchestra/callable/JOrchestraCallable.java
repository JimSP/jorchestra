package br.com.jorchestra.callable;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.jorchestra.canonical.JOrchestraHandle;
import br.com.jorchestra.util.JOrchestraContextUtils;

public class JOrchestraCallable implements Callable<Object>, Serializable {

	private static final long serialVersionUID = 6981532005948655731L;

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraCallable.class);

	private final JOrchestraHandle jOrchestraHandle;
	private final Class<?>[] parametersType;
	private final Object[] parameters;

	public JOrchestraCallable(final JOrchestraHandle jOrchestraHandle, final Class<?>[] parametersType,
			final Object[] parameters) {
		this.jOrchestraHandle = jOrchestraHandle;
		this.parametersType = parametersType;
		this.parameters = parameters;
	}

	@Override
	public Object call() throws Exception {
		LOGGER.debug("m=call, parameters=" + Arrays.toString(parameters));

		final Object jOrchestraBean = getBean();

		return invoke(jOrchestraBean);
	}
	
	public void execute() throws Exception {
		LOGGER.debug("m=execute, parameters=" + Arrays.toString(parameters));

		final Object jOrchestraBean = getBean();

		invoke(jOrchestraBean);
	}
	
	private Object getBean() {
		final Object jOrchestraBean = JOrchestraContextUtils
				.getJorchestraBean(jOrchestraHandle.getjOrchestraBeanName());
		return jOrchestraBean;
	}
	
	private Object invoke(final Object jOrchestraBean)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		return JOrchestraContextUtils
				.getMethosByJOrchestraPath(jOrchestraBean, jOrchestraHandle.getMethodName(), parametersType)
				.invoke(jOrchestraBean, parameters);
	}


}