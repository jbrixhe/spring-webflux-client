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
    private List<RequestInterceptor> requestInterceptors;
    private List<ResponseInterceptor> responseInterceptors;

    DefaultClientBuilder(ReactiveInvocationHandlerFactory reactiveInvocationHandlerFactory) {
        this.reactiveInvocationHandlerFactory = reactiveInvocationHandlerFactory;
        this.codecConfigurer = com.webfluxclient.codec.ExtendedClientCodecConfigurer.create();
        this.requestInterceptors = new ArrayList<>();
        this.responseInterceptors = new ArrayList<>();
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
    public ClientBuilder requestInterceptor(RequestInterceptor requestInterceptor) {
        requestInterceptors.add(requestInterceptor);
        return this;
    }

    @Override
    public ClientBuilder requestInterceptors(Consumer<List<RequestInterceptor>> requestInterceptorConsumer) {
        requestInterceptorConsumer.accept(requestInterceptors);
        return this;
    }

    @Override
    public ClientBuilder responseInterceptor(ResponseInterceptor responseInterceptor) {
        responseInterceptors.add(responseInterceptor);
        return this;
    }

    @Override
    public ClientBuilder responseInterceptors(Consumer<List<ResponseInterceptor>> responseInterceptorConsumer) {
        responseInterceptorConsumer.accept(responseInterceptors);
        return this;
    }

    @Override
    public <T> T build(Class<T> target, URI uri) {
        InvocationHandler invocationHandler = reactiveInvocationHandlerFactory.build(codecConfigurer, requestInterceptors, responseInterceptors, target, uri);
        return (T) Proxy.newProxyInstance(target.getClassLoader(), new Class<?>[]{target}, invocationHandler);
    }
}
