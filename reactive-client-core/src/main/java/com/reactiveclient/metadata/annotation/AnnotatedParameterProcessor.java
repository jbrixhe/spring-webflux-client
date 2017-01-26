package com.reactiveclient.metadata.annotation;

import com.reactiveclient.metadata.MethodMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public interface AnnotatedParameterProcessor {

    Class<? extends Annotation> getAnnotationType();

    void processAnnotation(MethodMetadata.Builder requestTemplateBuilder, Annotation annotation, Integer integer, Type parameterType);
}
