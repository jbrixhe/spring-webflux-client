package com.github.jbrixhe.reactiveclient.metadata.annotation;

import com.github.jbrixhe.reactiveclient.metadata.MethodMetadata;
import com.github.jbrixhe.reactiveclient.metadata.request.RequestTemplate;

import java.lang.annotation.Annotation;

public interface AnnotatedParameterProcessor {

    Class<? extends Annotation> getAnnotationType();

    void processAnnotation(MethodMetadata.Builder requestTemplateBuilder, Annotation annotation, Integer integer);
}
