package com.github.jbrixhe.reactiveclient.request;

import com.github.jbrixhe.reactiveclient.request.annotation.AnnotatedParameterProcessor;
import com.github.jbrixhe.reactiveclient.request.annotation.PathVariableParameterProcessor;
import com.github.jbrixhe.reactiveclient.request.annotation.RequestParamParameterProcessor;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestTemplateAnnotationVisitor {

    private Map<Class<? extends Annotation>, AnnotatedParameterProcessor> annotatedArgumentProcessors;

    public RequestTemplateAnnotationVisitor() {
        this.annotatedArgumentProcessors = Stream.of(
                new PathVariableParameterProcessor(),
                new RequestParamParameterProcessor())
                .collect(Collectors.toMap(AnnotatedParameterProcessor::getAnnotationType, Function.identity()));
    }

    public List<RequestTemplate> visit(Class<?> targetType) {
        RequestTemplate rootRequestTemplate = processRootRequestTemplate(targetType);
        List<RequestTemplate> result = new ArrayList<>();
        for (Method method : targetType.getMethods()) {
            if (method.getDeclaringClass() == Object.class || (method.getModifiers() & Modifier.STATIC) != 0) {
                continue;
            }
            RequestTemplate methodRequestTemplate = new RequestTemplate(rootRequestTemplate);
            processAnnotationOnMethod(method, methodRequestTemplate);
            result.add(methodRequestTemplate);
        }
        return result;
    }

    RequestTemplate processRootRequestTemplate(Class<?> targetType) {
        RequestTemplate rootRequestTemplate = new RequestTemplate();
        Assert.isTrue(targetType.getInterfaces().length <= 1, () -> "Invalid class " + targetType.getName() + ":Only one level of inheritance is currently supported");
        if (targetType.getInterfaces().length == 1) {
            AnnotationMetadata annotationMetadata = new StandardAnnotationMetadata(targetType.getInterfaces()[0]);
            processAnnotationOnClass(annotationMetadata, rootRequestTemplate);
        }
        AnnotationMetadata annotationMetadata = new StandardAnnotationMetadata(targetType);
        processAnnotationOnClass(annotationMetadata, rootRequestTemplate);

        return rootRequestTemplate;
    }

    private void processAnnotationOnMethod(Method method, RequestTemplate methodRequestTemplate) {
        MethodMetadata methodMetadata = new StandardMethodMetadata(method);
        processRequestMappingAnnotation(methodMetadata, methodRequestTemplate);

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        int count = parameterAnnotations.length;
        for (int i = 0; i < count; i++) {
            processAnnotationsOnParameter(methodRequestTemplate, parameterAnnotations[i], i);
        }
    }

    private void processAnnotationsOnParameter(RequestTemplate methodRequestTemplate, Annotation[] parameterAnnotation, int i) {
        if (parameterAnnotation != null) {
            for (Annotation annotation : parameterAnnotation) {
                AnnotatedParameterProcessor annotatedParameterProcessor;
                if (annotation != null && (annotatedParameterProcessor = annotatedArgumentProcessors.get(annotation.annotationType())) != null) {
                    annotatedParameterProcessor.processAnnotation(methodRequestTemplate, annotation, i);
                }
            }
        }
    }

    private void processAnnotationOnClass(AnnotationMetadata annotationMetadata, RequestTemplate requestTemplate) {
        processRequestMappingAnnotation(annotationMetadata, requestTemplate);
    }

    private void processRequestMappingAnnotation(AnnotatedTypeMetadata annotatedTypeMetadata, RequestTemplate requestTemplate) {
        Map<String, Object> requestMappingAttributes = annotatedTypeMetadata.getAnnotationAttributes(RequestMapping.class.getName());
        if (requestMappingAttributes != null && !requestMappingAttributes.isEmpty()) {
            parsePath(requestMappingAttributes, requestTemplate);
            parseMethod(requestMappingAttributes, requestTemplate);
            parseHeaders(requestMappingAttributes, requestTemplate);
        }
    }

    void parsePath(Map<String, Object> requestMappingAttributes, RequestTemplate requestTemplate) {
        String[] values = (String[]) requestMappingAttributes.get("value");
        Assert.isTrue(values.length <= 1, "Too many values on annotation RequestMapping");
        if (values.length > 0) {
            requestTemplate.getRequestPath().append(values[0]);
        }
    }

    void parseMethod(Map<String, Object> requestMappingAttributes, RequestTemplate requestTemplate) {
        RequestMethod[] methods = (RequestMethod[]) requestMappingAttributes.get("method");
        Assert.isTrue(methods.length <= 1, "Too many Request method for annotation");
        if (methods.length == 0) {
            requestTemplate.setMethod(HttpMethod.GET);
        } else {
            requestTemplate.setMethod(HttpMethod.valueOf(methods[0].name()));
        }
    }

    void parseHeaders(Map<String, Object> requestMappingAttributes, RequestTemplate requestTemplate) {
        String[] headers = (String[]) requestMappingAttributes.get("headers");
        if (headers.length > 0) {
            for (String header : headers) {
                extractHeader(header, requestTemplate);
            }
        }
    }

    void extractHeader(String header, RequestTemplate requestTemplate) {
        int index = header.indexOf('=');
        Assert.isTrue(index != -1, () -> String.format("Invalid request header [%s], the symbol '=' is required to separate the header name and value", header));

        String name = header.substring(0, index);
        Assert.isTrue(StringUtils.hasText(name), "Request header name can't not be empty");

        String value = header.substring(index + 1);
        Assert.isTrue(StringUtils.hasText(value), "Request header value can't not be empty");

        requestTemplate.addHeader(name, value);
    }
}
