package br.com.jorchestra.util;

import java.lang.reflect.Method;

import org.springframework.context.ApplicationContext;

public class JOrchestraContextUtils {

	private static ApplicationContext APPLICATION_CONTEXT;

	public static Method getMethosByJOrchestraPath(final Object jOrchestraBean, final String methodName,
			final Class<?>[] parameters) throws NoSuchMethodException, SecurityException {
		return jOrchestraBean.getClass().getMethod(methodName, parameters);
	}

	public static Object getJorchestraBean(final String jOrchestraBeanName) {
		return APPLICATION_CONTEXT.getBean(jOrchestraBeanName);
	}

	public static void setApplicationContext(final ApplicationContext applicationContext) {
		APPLICATION_CONTEXT = applicationContext;
	}

}
