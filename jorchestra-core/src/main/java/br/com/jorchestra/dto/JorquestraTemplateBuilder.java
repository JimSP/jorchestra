package br.com.jorchestra.dto;

import br.com.jorchestra.handle.JOrchestaType;

public final class JorquestraTemplateBuilder {

	public static JorquestraTemplateBuilder create() {
		return new JorquestraTemplateBuilder();
	}

	private JOrchestaType jOrchestaType;
	private String name;

	private JorquestraTemplateBuilder() {

	}

	public JorquestraTemplateBuilder withJOrchestaType(final JOrchestaType jOrchestaType) {
		this.jOrchestaType = jOrchestaType;
		return this;
	}

	public JorquestraTemplateBuilder withName(final String name) {
		this.name = name;
		return this;
	}

	public JOrquestraTemplate build() {
		return new JOrquestraTemplate(jOrchestaType, name);
	}
}
