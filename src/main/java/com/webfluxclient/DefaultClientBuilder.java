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

    DefaultClientBuilder(ReactiveInvocationHandlerFactory reactiveInvocationHandlerFactory) {
        this.reactiveInvocationHandlerFactory = reactiveInvocationHandlerFactory;
        this.codecConfigurer = ExtendedClientCodecConfigurer.create();
        this.requestInterceptors = new ArrayList<>();
    }

    @Override
    public ClientBuilder registerDefaultCodecs(boolean registerDefaults) {
        codecConfigurer.registerDefaults(registerDefaults);
        return this;
    }

    @Override
    public ClientBuilder defaultCodecs(Consumer<ExtendedClientCodecConfigurer.ExtendedClientDefaultCodecsConfigurer> defaultCodecsConfigurerConsumer) {
        defaultCodecsConfigurerConsumer.accept(codecConfigurer.defaultCodecs());
        return this;
    }

    @Override
    public ClientBuilder customCodecs(Consumer<ExtendedClientCodecConfigurer.ExtendedCustomCodecsConfigurer> customCodecsConfigurerConsumer) {
        customCodecsConfigurerConsumer.accept(codecConfigurer.customCodecs());
        return this;
    }

    @Override
    public ClientBuilder requestInterceptor(RequestInterceptor requestInterceptor) {
        requestInterceptors.add(requestInterceptor);
        return this;
    }

    @Override
    public <T> T build(Class<T> target, URI uri) {
        InvocationHandler invocationHandler = reactiveInvocationHandlerFactory.build(codecConfigurer, requestInterceptors, target, uri);
        return (T) Proxy.newProxyInstance(target.getClassLoader(), new Class<?>[]{target}, invocationHandler);
    }
}
