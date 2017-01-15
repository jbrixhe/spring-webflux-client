package com.github.jbrixhe.reactiveclient.request.annotation;

import com.github.jbrixhe.reactiveclient.request.RequestTemplate;

import java.lang.annotation.Annotation;

public interface AnnotatedParameterProcessor {

    Class<? extends Annotation> getAnnotationType();

    void processAnnotation(RequestTemplate.Builder requestTemplateBuilder, Annotation annotation, Integer integer);
}
