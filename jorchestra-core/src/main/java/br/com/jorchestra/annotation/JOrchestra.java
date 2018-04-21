package br.com.jorchestra.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Service;

import br.com.jorchestra.canonical.JOrchestraSignal;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface JOrchestra {

	public String path();

	public JOrchestraSignal jOrchestraSignalType() default JOrchestraSignal.MESSAGE;

	public boolean reliable() default true;
}
