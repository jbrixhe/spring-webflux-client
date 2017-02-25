package com.reactiveclient.starter;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

class ReactiveClientBeanRegister {
    private BeanDefinitionRegistry registry;

    private ResourceLoader resourceLoader;

    public ReactiveClientBeanRegister(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        this.registry = registry;
        this.resourceLoader = resourceLoader;
    }

    public void register(AnnotatedBeanDefinition annotatedBeanDefinition){
        AnnotationMetadata annotationMetadata = annotatedBeanDefinition.getMetadata();
        annotationMetadata.getEnclosingClassName();
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(ReactiveClientBeanFactory.class);
        Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(ReactiveClient.class.getName());
        definition.addPropertyValue("type", annotationMetadata.getClassName());
        definition.addPropertyValue("url", getUrl(attributes));
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setPrimary(true);

        String[] aliases = getAliases(attributes);

        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, annotationMetadata.getClassName(), aliases);
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    String getUrl(Map<String, Object> attributes) {
        String url = (String) attributes.get("url");
        if (StringUtils.hasText(url)){
            url = resolve(url);
            if (!url.contains("://")) {
                url = "http://" + url;
            }
            try {
                new URL(url);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(url + " is malformed", e);
            }
        }
        return url;
    }

    String[] getAliases(Map<String, Object> attributes) {
        String qualifier = (String) attributes.get("qualifier");
        if (StringUtils.hasText(qualifier)) {
            return new String[] {qualifier};
        }
        return new String[] {};
    }

    private String resolve(String value) {
        if (this.resourceLoader instanceof ConfigurableApplicationContext) {
            return ((ConfigurableApplicationContext) this.resourceLoader).getEnvironment().resolvePlaceholders(value);
        }
        return value;
    }
}
