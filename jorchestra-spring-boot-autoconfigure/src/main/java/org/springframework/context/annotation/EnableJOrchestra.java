package org.springframework.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(value = { JOrchestraAutoConfiguration.class, JOrchestaEventConguration.class })
public @interface EnableJOrchestra {

}
