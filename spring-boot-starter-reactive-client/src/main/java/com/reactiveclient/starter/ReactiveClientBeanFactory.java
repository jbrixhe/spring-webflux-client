package com.reactiveclient.starter;

import com.reactiveclient.ErrorDecoder;
import com.reactiveclient.ReactiveClientBuilder;
import com.reactiveclient.RequestInterceptor;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

@Setter
public class ReactiveClientBeanFactory implements
        FactoryBean<Object>,
        ApplicationContextAware {

    private Class<?> type;

    private String url;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object getObject() throws Exception {
        Map<String, ErrorDecoder> errorDecoders = applicationContext.getBeansOfType(ErrorDecoder.class);
        Map<String, RequestInterceptor> requestInterceptors = applicationContext.getBeansOfType(RequestInterceptor.class);

        return ReactiveClientBuilder
                .builder()
                .errorDecoders(errorDecoders.values())
                .requestInterceptors(requestInterceptors.values())
                .build(type, url);
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }
}
