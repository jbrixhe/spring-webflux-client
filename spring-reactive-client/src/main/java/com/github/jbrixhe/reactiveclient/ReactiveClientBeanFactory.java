package com.github.jbrixhe.reactiveclient;

import com.github.jbrixhe.reactiveclient.request.ReactiveContext;
import com.github.jbrixhe.reactiveclient.request.RequestHandler;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Proxy;

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
        ReactiveContext reactiveContext = new ReactiveContext(url);
        return Proxy.newProxyInstance(classLoader, new Class<?>[]{type}, new RequestHandler(reactiveContext));
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }
}
