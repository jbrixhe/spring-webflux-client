package com.reactiveclient.starter;

import com.reactiveclient.ReactiveClientBuilder;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.URI;

@Setter
public class ReactiveClientBeanFactory implements
        FactoryBean<Object>,
        InitializingBean,
        ApplicationContextAware,
        BeanClassLoaderAware {

    private Class<?> type;

    private String url;

    private ApplicationContext applicationContext;

    private ClassLoader classLoader;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Object getObject() throws Exception {
        return ReactiveClientBuilder
                .builder()
                .build(type, url);
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }
}
