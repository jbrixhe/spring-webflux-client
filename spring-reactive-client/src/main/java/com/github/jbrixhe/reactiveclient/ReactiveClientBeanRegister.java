package com.github.jbrixhe.reactiveclient;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

class ReactiveClientBeanRegister {
    private BeanDefinitionRegistry registry;

    public ReactiveClientBeanRegister(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public void register(AnnotatedBeanDefinition annotatedBeanDefinition){
        AnnotationMetadata annotationMetadata = annotatedBeanDefinition.getMetadata();
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(ReactiveClientBeanFactory.class);
        definition.addPropertyValue("type", annotationMetadata.getClassName());
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setPrimary(true);

        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, annotationMetadata.getClassName(), new String[] {});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }
}
