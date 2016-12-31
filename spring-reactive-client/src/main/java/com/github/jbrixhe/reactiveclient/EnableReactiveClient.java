package com.github.jbrixhe.reactiveclient;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ReactiveClientRegistrar.class)
public @interface EnableReactiveClient {
    String[] value() default {};

    String[] basePackages() default {};

}
