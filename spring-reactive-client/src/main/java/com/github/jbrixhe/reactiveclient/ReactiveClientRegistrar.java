package com.github.jbrixhe.reactiveclient;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReactiveClientRegistrar implements
        ImportBeanDefinitionRegistrar,
        ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    public ReactiveClientRegistrar() {
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanner = new ReactiveClientCandidateComponentProvider(resourceLoader);
        ReactiveClientBeanRegister reactiveClientBeanRegister = new ReactiveClientBeanRegister(registry);
        for (String basePackage : getPackagesToScan(metadata)) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    reactiveClientBeanRegister.register((AnnotatedBeanDefinition)candidateComponent);
                }
            }
        }
    }

    Set<String> getPackagesToScan(AnnotationMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(EnableReactiveClient.class.getCanonicalName());
        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) attributes.get("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }

        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }

        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(metadata.getClassName()));
        }
        return basePackages;
    }
}
