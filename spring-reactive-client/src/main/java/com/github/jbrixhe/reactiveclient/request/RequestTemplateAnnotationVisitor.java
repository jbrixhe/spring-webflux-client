package com.github.jbrixhe.reactiveclient.request;

import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestTemplateAnnotationVisitor {

    public List<RequestTemplate> visit(Class<?> targetType) {
        RequestTemplate rootRequestTemplate = processRootRequestTemplate(targetType);
        List<RequestTemplate> result = new ArrayList<>();
        for (Method method : targetType.getMethods()) {
            if (method.getDeclaringClass() == Object.class || (method.getModifiers() & Modifier.STATIC) != 0) {
                continue;
            }
            MethodMetadata methodMetadata = new StandardMethodMetadata(method);
            RequestTemplate methodRequestTemplate = new RequestTemplate(rootRequestTemplate);
            processAnnotationOnMethod(methodMetadata, methodRequestTemplate);
            result.add(methodRequestTemplate);
        }
        return result;
    }

    private void processAnnotationOnMethod(MethodMetadata methodMetadata, RequestTemplate methodRequestTemplate) {
        processRequestMappingAnnotation(methodMetadata, methodRequestTemplate);
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

    private void processAnnotationOnClass(AnnotationMetadata annotationMetadata, RequestTemplate requestTemplate) {
        processRequestMappingAnnotation(annotationMetadata, requestTemplate);
    }

    private void processRequestMappingAnnotation(AnnotatedTypeMetadata annotatedTypeMetadata, RequestTemplate requestTemplate) {
        Map<String, Object> requestMappingAttributes = annotatedTypeMetadata.getAnnotationAttributes(RequestMapping.class.getName());
        if (requestMappingAttributes != null && !requestMappingAttributes.isEmpty()) {
            parsePath(requestMappingAttributes, requestTemplate);;
            parseMethod(requestMappingAttributes, requestTemplate);;
        }
    }

    void parsePath(Map<String, Object> requestMappingAttributes, RequestTemplate requestTemplate) {
        String[] values = (String[]) requestMappingAttributes.get("value");
        Assert.isTrue(values.length <= 1, () -> "Too many values on annotation RequestMapping");
        if (values.length > 0) {
            requestTemplate.getRequestPath().append(values[0]);
        }
    }

    void parseMethod(Map<String, Object> requestMappingAttributes, RequestTemplate requestTemplate) {
        RequestMethod[] methods = (RequestMethod[]) requestMappingAttributes.get("method");
        Assert.isTrue(methods.length <= 1, () -> "Too many Request mathod for annotation");
        if (methods.length == 0) {
            requestTemplate.setMethod(HttpMethod.GET);
        } else {
            requestTemplate.setMethod(HttpMethod.valueOf(methods[0].name()));
        }
    }
}
