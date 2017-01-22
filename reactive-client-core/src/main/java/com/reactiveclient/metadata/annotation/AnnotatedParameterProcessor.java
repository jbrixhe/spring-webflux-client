package com.reactiveclient.metadata.annotation;

import com.reactiveclient.metadata.MethodMetadata;

import java.lang.annotation.Annotation;

public interface AnnotatedParameterProcessor {

    Class<? extends Annotation> getAnnotationType();

    void processAnnotation(MethodMetadata.Builder requestTemplateBuilder, Annotation annotation, Integer integer);
}
