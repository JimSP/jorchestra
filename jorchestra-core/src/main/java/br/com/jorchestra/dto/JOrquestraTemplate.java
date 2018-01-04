package br.com.jorchestra.dto;

import br.com.jorchestra.handle.JOrchestaType;

public class JOrquestraTemplate {

	public static JorquestraTemplateBuilder create() {
		return JorquestraTemplateBuilder.create();
	}

	private final JOrchestaType jOrchestaType;
	private final String name;

	public JOrquestraTemplate(final JOrchestaType jOrchestaType, final String name) {
		super();
		this.jOrchestaType = jOrchestaType;
		this.name = name;
	}

	public JOrchestaType getjOrchestaType() {
		return jOrchestaType;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "JorquestraTemplate [jOrchestaType=" + jOrchestaType + ", name=" + name + "]";
	}
}
