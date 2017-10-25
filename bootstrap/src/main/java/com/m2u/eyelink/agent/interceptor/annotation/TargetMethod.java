package com.m2u.eyelink.agent.interceptor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TargetMethod {
    /**
     * target method name
     */
    String name();
    
    /**
     * target method parameter types
     */
    String[] paramTypes() default {};
}
