package com.github.jbrixhe.reactiveclient.request;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RequestTemplateAnnotationVisitor {

    public List<RequestTemplate> visit(Class<?> targetType) {
        return Collections.emptyList();
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
        Map<String, Object> requestMappingAttributes = annotationMetadata.getAnnotationAttributes(RequestMapping.class.getName());
        if (requestMappingAttributes != null && !requestMappingAttributes.isEmpty()) {
            processRequestMappingAnnotation(requestMappingAttributes, requestTemplate);
        }
    }

    private void processRequestMappingAnnotation(Map<String, Object> requestMappingAttributes, RequestTemplate requestTemplate) {
        parsePath(requestMappingAttributes, requestTemplate);
    }

    void parsePath(Map<String, Object> requestMappingAttributes, RequestTemplate requestTemplate) {
        String[] values = (String[]) requestMappingAttributes.get("value");
        Assert.isTrue(values.length <= 1, () -> "Too many values on annotation RequestMapping");
        if (values.length > 0) {
            requestTemplate.getRequestPath().append(values[0]);
        }
    }
}
