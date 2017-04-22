package com.webfluxclient.metadata.annotation;

import com.webfluxclient.metadata.MethodMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface AnnotatedParameterProcessor {

    Class<? extends Annotation> getAnnotationType();

    void processAnnotation(MethodMetadata.Builder requestTemplateBuilder, Annotation annotation, Integer integer, Type parameterType);
}
