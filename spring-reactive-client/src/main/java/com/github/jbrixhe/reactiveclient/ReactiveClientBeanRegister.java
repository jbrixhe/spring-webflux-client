package com.github.jbrixhe.reactiveclient;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

class ReactiveClientBeanRegister {
    private BeanDefinitionRegistry registry;

    public ReactiveClientBeanRegister(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public void register(AnnotatedBeanDefinition annotatedBeanDefinition){

    }
}
