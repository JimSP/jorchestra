package br.com.jorchestra.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import br.com.jorchestra.annotation.JOrchestra;
import br.com.jorchestra.dto.JOrchestraBeanResponse;
import br.com.jorchestra.dto.JOrchestraMonitorResponseBuilder;
import br.com.jorchestra.util.JOrchestraContextUtils;
import br.com.jorchestra.util.JOrchestraHandleUtils;

@JOrchestra(path = "jorchestra")
public class JOrchestraBeans {

	@Autowired
	private ApplicationContext applicationContext;

	public List<JOrchestraBeanResponse> beans() {
		final List<JOrchestraBeanResponse> list = new ArrayList<>();
		JOrchestraContextUtils.jorchestraHandleConsumer(applicationContext, jOrchestraHandle -> {

			final String jOrchestraBeanName = jOrchestraHandle.getjOrchestraBeanName();
			final Object jOrchestraBean = applicationContext.getBean(jOrchestraBeanName);
			final String methodName = jOrchestraHandle.getMethodName();
			final Class<?>[] parametersType = jOrchestraHandle.getJorchestraParametersType();
			final JOrchestraMonitorResponseBuilder jOrchestraMonitorResponseBuilder = JOrchestraBeanResponse.create();

			try {
				final Method method = jOrchestraBean.getClass().getDeclaredMethod(methodName, parametersType);
				jOrchestraMonitorResponseBuilder //
						.withjOrchestraBeanName(jOrchestraHandle.getjOrchestraBeanName()) //
						.withjOrchestraPah(jOrchestraHandle.getJOrchestraPath()) //
						.withRequestTemplate(JOrchestraHandleUtils.getJOrchestraRequestTemplate(method)) //
						.withResponseTemplate(JOrchestraHandleUtils.getJOrchestraResponseTemplate(method));

			} catch (NoSuchMethodException | SecurityException e) {
				jOrchestraMonitorResponseBuilder.withMessage("m=beans, jOrchestraHandle=" + jOrchestraHandle
						+ ", msg=\"error during template creation, verify method name and access modifier.\"");
			} finally {
				list.add(jOrchestraMonitorResponseBuilder.build());
			}
		});

		return list;
	}
}
