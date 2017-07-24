package com.webfluxclient;

import com.webfluxclient.codec.ExtendedClientCodecConfigurer;
import com.webfluxclient.handler.ReactiveInvocationHandlerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

class DefaultClientBuilder implements ClientBuilder {
    private ReactiveInvocationHandlerFactory reactiveInvocationHandlerFactory;
    private ExtendedClientCodecConfigurer codecConfigurer;
    private List<RequestProcessor> requestProcessors;
    private List<ResponseProcessor> responseProcessors;
    private Logger logger;
    private LogLevel logLevel;

    DefaultClientBuilder(ReactiveInvocationHandlerFactory reactiveInvocationHandlerFactory) {
        this.reactiveInvocationHandlerFactory = reactiveInvocationHandlerFactory;
        this.codecConfigurer = com.webfluxclient.codec.ExtendedClientCodecConfigurer.create();
        this.requestProcessors = new ArrayList<>();
        this.responseProcessors = new ArrayList<>();
    }

    @Override
    public ClientBuilder registerDefaultCodecs(boolean registerDefaults) {
        codecConfigurer.registerDefaults(registerDefaults);
        return this;
    }

    @Override
    public ClientBuilder defaultCodecs(Consumer<ExtendedClientCodecConfigurer.ExtendedClientDefaultCodecs> defaultCodecsConfigurerConsumer) {
        defaultCodecsConfigurerConsumer.accept(codecConfigurer.defaultCodecs());
        return this;
    }

    @Override
    public ClientBuilder customCodecs(Consumer<ExtendedClientCodecConfigurer.ExtendedCustomCodecs> customCodecsConfigurerConsumer) {
        customCodecsConfigurerConsumer.accept(codecConfigurer.customCodecs());
        return this;
    }

    @Override
    public ClientBuilder logger(Logger logger) {
        this.logger = logger;
        return this;
    }

    @Override
    public ClientBuilder logLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    @Override
    public ClientBuilder requestProcessor(RequestProcessor requestProcessor) {
        requestProcessors.add(requestProcessor);
        return this;
    }

    @Override
    public ClientBuilder requestProcessors(Consumer<List<RequestProcessor>> requestInterceptorConsumer) {
        requestInterceptorConsumer.accept(requestProcessors);
        return this;
    }

    @Override
    public ClientBuilder responseProcessor(ResponseProcessor responseProcessor) {
        responseProcessors.add(responseProcessor);
        return this;
    }

    @Override
    public ClientBuilder responseProcessors(Consumer<List<ResponseProcessor>> responseInterceptorConsumer) {
        responseInterceptorConsumer.accept(responseProcessors);
        return this;
    }

    @Override
    public <T> T build(Class<T> target, URI uri) {
        InvocationHandler invocationHandler = reactiveInvocationHandlerFactory.build(codecConfigurer, requestProcessors, responseProcessors, logger, logLevel, target, uri);
        return (T) Proxy.newProxyInstance(target.getClassLoader(), new Class<?>[]{target}, invocationHandler);
    }
}
