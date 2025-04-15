package org.remote.desktop.conditional;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Conditional(FileNotExistsCondition.class)
public @interface ConditionalFilePresence {
    String value();
    boolean present() default false;
}