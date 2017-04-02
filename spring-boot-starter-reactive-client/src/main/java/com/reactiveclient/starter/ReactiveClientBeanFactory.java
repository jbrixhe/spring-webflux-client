package com.reactiveclient.starter;

import com.reactiveclient.ErrorDecoder;
import com.reactiveclient.ReactiveClientBuilder;
import com.reactiveclient.metadata.request.ReactiveRequest;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ResolvableType;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        List<Consumer<ReactiveRequest>> requestInterceptorBeans = Stream.of(applicationContext.getBeanNamesForType(ResolvableType.forClassWithGenerics(Consumer.class, ReactiveRequest.class)))
                .map(applicationContext::getBean)
                .map(bean -> (Consumer<ReactiveRequest>)bean)
                .collect(Collectors.toList());

        return ReactiveClientBuilder
                .builder()
                .errorDecoders(errorDecoders.values())
                .requestInterceptors(requestInterceptorBeans)
                .build(type, url);
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }
}
