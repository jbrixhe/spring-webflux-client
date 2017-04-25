package com.webfluxclient;

import com.webfluxclient.client.codec.ExtendedClientCodecConfigurer;
import com.webfluxclient.handler.DefaultReactiveInvocationHandlerFactory;
import com.webfluxclient.handler.ReactiveInvocationHandlerFactory;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

class DefaultClientBuilder implements ClientBuilder {
    private ReactiveInvocationHandlerFactory reactiveInvocationHandlerFactory = new DefaultReactiveInvocationHandlerFactory();
    private ExtendedClientCodecConfigurer codecConfigurer;
    private List<RequestInterceptor> requestInterceptors;

    DefaultClientBuilder() {
        this.codecConfigurer = ExtendedClientCodecConfigurer.create();
        this.requestInterceptors = new ArrayList<>();
    }

    @Override
    public ClientBuilder registerDefaultCodecs(boolean registerDefaults) {
        codecConfigurer.registerDefaults(registerDefaults);
        return this;
    }

    @Override
    public ClientBuilder errorDecoder(ErrorDecoder errorDecoder) {
        codecConfigurer.customCodecs().errorDecoder(errorDecoder);
        return this;
    }

    @Override
    public ClientBuilder errorReader(HttpErrorReader httpErrorReader) {
        codecConfigurer.customCodecs().errorReader(httpErrorReader);
        return this;
    }

    @Override
    public ClientBuilder decoder(Decoder<?> decoder) {
        codecConfigurer.customCodecs().decoder(decoder);
        return this;
    }

    @Override
    public ClientBuilder messageReader(HttpMessageReader<?> httpMessageReader) {
        codecConfigurer.customCodecs().reader(httpMessageReader);
        return this;
    }

    @Override
    public ClientBuilder encoder(Encoder<?> encoder) {
        codecConfigurer.customCodecs().encoder(encoder);
        return this;
    }

    @Override
    public ClientBuilder messageWriter(HttpMessageWriter<?> httpMessageWriter) {
        codecConfigurer.customCodecs().writer(httpMessageWriter);
        return this;
    }

    @Override
    public ClientBuilder requestInterceptor(RequestInterceptor requestInterceptor) {
        requestInterceptors.add(requestInterceptor);
        return this;
    }

    @Override
    public <T> T build(Class<T> target, URI uri) {
        RequestInterceptor requestInterceptor = requestInterceptors.stream()
                .reduce(RequestInterceptor::andThen)
                .orElse(reactiveRequest ->{});

        InvocationHandler invocationHandler = reactiveInvocationHandlerFactory.build(codecConfigurer, requestInterceptor, target, uri);

        return (T) Proxy.newProxyInstance(target.getClassLoader(), new Class<?>[]{target}, invocationHandler);
    }
}
