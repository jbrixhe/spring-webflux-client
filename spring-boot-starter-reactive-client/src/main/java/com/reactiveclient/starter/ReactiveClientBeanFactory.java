package com.reactiveclient.starter;

import com.reactiveclient.ClientBuilder;
import com.reactiveclient.HttpErrorReader;
import com.reactiveclient.RequestInterceptor;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;

import java.net.URI;

@Setter
public class ReactiveClientBeanFactory implements
        FactoryBean<Object>,
        ApplicationContextAware {

    private Class<?> type;

    private URI url;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object getObject() throws Exception {
        ClientBuilder builder = ClientBuilder.builder();

        applicationContext.getBeansOfType(RequestInterceptor.class).values().forEach(builder::requestInterceptor);
        applicationContext.getBeansOfType(HttpMessageWriter.class).values().forEach(builder::messageWriter);
        applicationContext.getBeansOfType(HttpMessageReader.class).values().forEach(builder::messageReader);
        applicationContext.getBeansOfType(HttpErrorReader.class).values().forEach(builder::errorReader);

        return builder.build(type, url);
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }
}
