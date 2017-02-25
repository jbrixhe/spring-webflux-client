package com.reactiveclient.metadata;

import com.reactiveclient.metadata.annotation.AnnotatedParameterProcessor;
import com.reactiveclient.metadata.annotation.PathVariableParameterProcessor;
import com.reactiveclient.metadata.annotation.RequestBodyParameterProcessor;
import com.reactiveclient.metadata.annotation.RequestHeaderParameterProcessor;
import com.reactiveclient.metadata.annotation.RequestParamParameterProcessor;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
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
import java.lang.reflect.Parameter;
import java.net.URI;
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
                new RequestHeaderParameterProcessor(),
                new RequestBodyParameterProcessor())
                .collect(Collectors.toMap(AnnotatedParameterProcessor::getAnnotationType, Function.identity()));
    }

    public List<MethodMetadata> build(Class<?> target, URI uri) {
        MethodMetadata rootRequestTemplate = processTarget(target, uri);
        List<MethodMetadata> result = new ArrayList<>();
        for (Method method : target.getMethods()) {
            if (method.getDeclaringClass() == Object.class || (method.getModifiers() & Modifier.STATIC) != 0) {
                continue;
            }
            MethodMetadata.Builder requestTemplateBuilder = MethodMetadata.newBuilder(rootRequestTemplate)
                    .targetMethod(method);

            processAnnotationOnMethod(method, requestTemplateBuilder);

            result.add(requestTemplateBuilder.build());
        }
        return result;
    }

    MethodMetadata processTarget(Class<?> target, URI uri) {
        MethodMetadata.Builder rootRequestTemplate = MethodMetadata.newBuilder(uri)
                .addPath(uri.getPath());

        Assert.isTrue(target.getInterfaces().length <= 1, () -> "Invalid class " + target.getName() + ":Only one level of inheritance is currently supported");
        if (target.getInterfaces().length == 1) {
            AnnotationMetadata annotationMetadata = new StandardAnnotationMetadata(target.getInterfaces()[0]);
            processAnnotationOnClass(annotationMetadata, rootRequestTemplate);
        }
        AnnotationMetadata annotationMetadata = new StandardAnnotationMetadata(target);
        processAnnotationOnClass(annotationMetadata, rootRequestTemplate);

        return rootRequestTemplate.build();
    }

    private void processAnnotationOnMethod(Method method, MethodMetadata.Builder requestTemplateBuilder) {
        AnnotatedTypeMetadata methodMetadata = new StandardMethodMetadata(method);
        Parameter[] parameters = method.getParameters();

        processRequestMappingAnnotation(methodMetadata, requestTemplateBuilder);

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            processAnnotationsOnParameter(requestTemplateBuilder, parameter, i);
            if (parameter.getAnnotations().length == 0) {
                requestTemplateBuilder.body(i, parameter.getType());
            }
        }
    }

    private void processAnnotationsOnParameter(MethodMetadata.Builder requestTemplateBuilder, Parameter parameter, int i) {
        if (parameter.getAnnotations() != null) {
            for (Annotation annotation : parameter.getAnnotations()) {
                AnnotatedParameterProcessor annotatedParameterProcessor;
                if (annotation != null && (annotatedParameterProcessor = annotatedArgumentProcessors.get(annotation.annotationType())) != null) {
                    annotatedParameterProcessor.processAnnotation(requestTemplateBuilder, annotation, i, parameter.getParameterizedType());
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
            parseConsumes(requestMappingAttributes, requestTemplateBuilder);
            parseProduces(requestMappingAttributes, requestTemplateBuilder);
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

    private void parseProduces(Map<String, Object> requestMappingAttributes, MethodMetadata.Builder requestTemplateBuilder) {
        String[] produces = (String[]) requestMappingAttributes.get("produces");
        Assert.isTrue(produces.length <= 1, "Too many produces parameter for annotation");
        if (produces.length > 0) {
            requestTemplateBuilder.addHeader("Content-Type", produces[0]);
        }
    }

    private void parseConsumes(Map<String, Object> requestMappingAttributes, MethodMetadata.Builder requestTemplateBuilder) {
        String[] consumes = (String[]) requestMappingAttributes.get("consumes");
        Assert.isTrue(consumes.length <= 1, "Too many consumes parameter for annotation");
        if (consumes.length > 0) {
            requestTemplateBuilder.addHeader("Accept", consumes[0]);
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
