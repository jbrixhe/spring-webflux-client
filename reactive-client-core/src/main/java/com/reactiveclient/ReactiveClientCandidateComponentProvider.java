package com.reactiveclient;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

class ReactiveClientCandidateComponentProvider extends ClassPathScanningCandidateComponentProvider {
    public ReactiveClientCandidateComponentProvider(ResourceLoader resourceLoader) {
        super(false);
        setResourceLoader(resourceLoader);
        addIncludeFilter(new AnnotationTypeFilter(ReactiveClient.class));
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isIndependent() && !metadata.isAnnotation() && metadata.isInterface();
    }
}
