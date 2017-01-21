package com.github.jbrixhe.reactiveclient.metadata;

import com.github.jbrixhe.reactiveclient.metadata.annotation.AnnotatedParameterProcessor;
import com.github.jbrixhe.reactiveclient.metadata.annotation.PathVariableParameterProcessor;
import com.github.jbrixhe.reactiveclient.metadata.annotation.RequestHeaderParameterProcessor;
import com.github.jbrixhe.reactiveclient.metadata.annotation.RequestParamParameterProcessor;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MethodMetadataFactory {

    private Map<Class<? extends Annotation>, AnnotatedParameterProcessor> annotatedArgumentProcessors;

    public MethodMetadataFactory() {
        this.annotatedArgumentProcessors = Stream.of(
                new PathVariableParameterProcessor(),
                new RequestParamParameterProcessor(),
                new RequestHeaderParameterProcessor())
                .collect(Collectors.toMap(AnnotatedParameterProcessor::getAnnotationType, Function.identity()));
    }

    public List<MethodMetadata> build(Class<?> targetClass) {
        MethodMetadata rootRequestTemplate = processTargetClass(targetClass);
        List<MethodMetadata> result = new ArrayList<>();
        for (Method method : targetClass.getMethods()) {
            if (method.getDeclaringClass() == Object.class || (method.getModifiers() & Modifier.STATIC) != 0) {
                continue;
            }
            MethodMetadata.Builder requestTemplateBuilder = MethodMetadata.newBuilder(rootRequestTemplate)
                    .targerMethod(method);
            parseMethodReturnType(method, requestTemplateBuilder);
            processAnnotationOnMethod(method, requestTemplateBuilder);
            result.add(requestTemplateBuilder.build());
        }
        return result;
    }

    MethodMetadata processTargetClass(Class<?> targetType) {
        MethodMetadata.Builder rootRequestTemplate = MethodMetadata.newBuilder();
        Assert.isTrue(targetType.getInterfaces().length <= 1, () -> "Invalid class " + targetType.getName() + ":Only one level of inheritance is currently supported");
        if (targetType.getInterfaces().length == 1) {
            AnnotationMetadata annotationMetadata = new StandardAnnotationMetadata(targetType.getInterfaces()[0]);
            processAnnotationOnClass(annotationMetadata, rootRequestTemplate);
        }
        AnnotationMetadata annotationMetadata = new StandardAnnotationMetadata(targetType);
        processAnnotationOnClass(annotationMetadata, rootRequestTemplate);

        return rootRequestTemplate.build();
    }

    private void processAnnotationOnMethod(Method method, MethodMetadata.Builder requestTemplateBuilder) {
        AnnotatedTypeMetadata methodMetadata = new StandardMethodMetadata(method);
        processRequestMappingAnnotation(methodMetadata, requestTemplateBuilder);
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        int count = parameterAnnotations.length;
        for (int i = 0; i < count; i++) {
            processAnnotationsOnParameter(requestTemplateBuilder, parameterAnnotations[i], i);
        }
    }

    private void processAnnotationsOnParameter(MethodMetadata.Builder requestTemplateBuilder, Annotation[] parameterAnnotation, int i) {
        if (parameterAnnotation != null) {
            for (Annotation annotation : parameterAnnotation) {
                AnnotatedParameterProcessor annotatedParameterProcessor;
                if (annotation != null && (annotatedParameterProcessor = annotatedArgumentProcessors.get(annotation.annotationType())) != null) {
                    annotatedParameterProcessor.processAnnotation(requestTemplateBuilder, annotation, i);
                }
            }
        }
    }

    private void processAnnotationOnClass(AnnotationMetadata annotationMetadata, MethodMetadata.Builder requestTemplateBuilder) {
        processRequestMappingAnnotation(annotationMetadata, requestTemplateBuilder);
    }

    private void processRequestMappingAnnotation(AnnotatedTypeMetadata annotatedTypeMetadata, MethodMetadata.Builder requestTemplateBuilder) {
        Map<String, Object> requestMappingAttributes = annotatedTypeMetadata.getAnnotationAttributes(RequestMapping.class.getName());
        if (requestMappingAttributes != null && !requestMappingAttributes.isEmpty()) {
            parsePath(requestMappingAttributes, requestTemplateBuilder);
            parseMethod(requestMappingAttributes, requestTemplateBuilder);
            parseHeaders(requestMappingAttributes, requestTemplateBuilder);
        }
    }

    void parsePath(Map<String, Object> requestMappingAttributes, MethodMetadata.Builder requestTemplateBuilder) {
        String[] values = (String[]) requestMappingAttributes.get("value");
        Assert.isTrue(values.length <= 1, "Too many values on annotation RequestMapping");
        if (values.length > 0) {
            requestTemplateBuilder.addPath(values[0]);
        }
    }

    void parseMethod(Map<String, Object> requestMappingAttributes, MethodMetadata.Builder requestTemplateBuilder) {
        RequestMethod[] methods = (RequestMethod[]) requestMappingAttributes.get("method");
        Assert.isTrue(methods.length <= 1, "Too many Request httpMethod for annotation");
        if (methods.length == 0) {
            requestTemplateBuilder.httpMethod(HttpMethod.GET);
        } else {
            requestTemplateBuilder.httpMethod(HttpMethod.valueOf(methods[0].name()));
        }
    }

    void parseHeaders(Map<String, Object> requestMappingAttributes, MethodMetadata.Builder requestTemplateBuilder) {
        String[] headers = (String[]) requestMappingAttributes.get("headers");
        if (headers.length > 0) {
            for (String header : headers) {
                extractHeader(header, requestTemplateBuilder);
            }
        }
    }

    void parseMethodReturnType(Method method, MethodMetadata.Builder requestTemplateBuilder) {
        Type returnType = method.getGenericReturnType();
        if (ParameterizedType.class.isInstance(returnType)) {
            ParameterizedType parameterizedType = (ParameterizedType)returnType;
            Type argumentType = parameterizedType.getActualTypeArguments()[0];
            if (ParameterizedType.class.isInstance(argumentType)) {
                throw new IllegalArgumentException("Embedded generic type not supported yet.");
            }

            if (Mono.class.equals(parameterizedType.getRawType())) {
                requestTemplateBuilder.returnType(ReturnType.monoOf((Class<?>) argumentType));
            } else if (Flux.class.equals(parameterizedType.getRawType())) {
                requestTemplateBuilder.returnType(ReturnType.fluxOf((Class<?>) argumentType));
            }
        } else if (void.class.equals(returnType)) {
            requestTemplateBuilder.returnType(ReturnType.none());
        }
    }

    void extractHeader(String header, MethodMetadata.Builder requestTemplateBuilder) {
        int index = header.indexOf('=');
        Assert.isTrue(index != -1, () -> String.format("Invalid apply header [%s], the symbol '=' is required to separate the header name and value", header));

        String name = header.substring(0, index);
        Assert.isTrue(StringUtils.hasText(name), "Request header name can't not be empty");

        String value = header.substring(index + 1);
        Assert.isTrue(StringUtils.hasText(value), "Request header value can't not be empty");

        requestTemplateBuilder.addHeader(name, value);
    }
}
