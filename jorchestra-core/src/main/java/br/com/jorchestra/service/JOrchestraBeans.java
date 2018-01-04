package br.com.jorchestra.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import br.com.jorchestra.annotation.JOrchestra;
import br.com.jorchestra.dto.JOrquestraBeanResponse;
import br.com.jorchestra.util.JOrchestraContextUtils;

@JOrchestra(path = "jorchestra")
public class JOrchestraBeans {

	@Autowired
	private ApplicationContext applicationContext;

	public List<JOrquestraBeanResponse> beans() {
		final List<JOrquestraBeanResponse> list = new ArrayList<>();

		JOrchestraContextUtils.jorchestraHandleConsumer(applicationContext,
				jOrchestraHandle -> list.add(JOrquestraBeanResponse.create() //
						.withjOrchestraBeanName(jOrchestraHandle.getjOrchestraBeanName()) //
						.withjOrchestraPah(jOrchestraHandle.getJOrchestraPath()) //
						.withRequestTemplate(jOrchestraHandle.getJOrchestraRequestTemplate()) //
						.withResponseTemplate(jOrchestraHandle.getJOrchestraResponseTemplate()) //
						.build()));

		return list;
	}
}
