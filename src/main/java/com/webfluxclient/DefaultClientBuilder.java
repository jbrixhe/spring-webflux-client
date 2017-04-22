package com.webfluxclient;

import com.webfluxclient.client.DefaultWebClientFactory;
import com.webfluxclient.client.codec.ExtendedClientCodecConfigurer;
import com.webfluxclient.handler.DefaultReactiveInvocationHandlerFactory;
import com.webfluxclient.handler.ReactiveInvocationHandlerFactory;
import com.webfluxclient.metadata.MethodMetadata;
import com.webfluxclient.metadata.MethodMetadataFactory;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

class DefaultClientBuilder implements ClientBuilder {
    private MethodMetadataFactory methodMetadataFactory = new MethodMetadataFactory();
    private ReactiveInvocationHandlerFactory reactiveInvocationHandlerFactory = new DefaultReactiveInvocationHandlerFactory();
    private DefaultWebClientFactory defaultWebClientFactory = new DefaultWebClientFactory();
    private ExtendedClientCodecConfigurer codecs;
    private List<RequestInterceptor> requestInterceptors;

    DefaultClientBuilder() {
        this.codecs = ExtendedClientCodecConfigurer.create();
        this.requestInterceptors = new ArrayList<>();
    }

    @Override
    public ClientBuilder registerDefaultCodecs(boolean registerDefaults) {
        codecs.registerDefaults(registerDefaults);
        return this;
    }

    @Override
    public ClientBuilder errorDecoder(ErrorDecoder errorDecoder) {
        codecs.customCodecs().errorDecoder(errorDecoder);
        return this;
    }

    @Override
    public ClientBuilder errorReader(HttpErrorReader httpErrorReader) {
        codecs.customCodecs().errorReader(httpErrorReader);
        return this;
    }

    @Override
    public ClientBuilder decoder(Decoder<?> decoder) {
        codecs.customCodecs().decoder(decoder);
        return this;
    }

    @Override
    public ClientBuilder messageReader(HttpMessageReader<?> httpMessageReader) {
        codecs.customCodecs().reader(httpMessageReader);
        return this;
    }

    @Override
    public ClientBuilder encoder(Encoder<?> encoder) {
        codecs.customCodecs().encoder(encoder);
        return this;
    }

    @Override
    public ClientBuilder messageWriter(HttpMessageWriter<?> httpMessageWriter) {
        codecs.customCodecs().writer(httpMessageWriter);
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

        WebClient webClient = defaultWebClientFactory.create(codecs);
        List<MethodMetadata> methodMetadatas = methodMetadataFactory.build(target, uri);
        InvocationHandler invocationHandler = reactiveInvocationHandlerFactory.create(methodMetadatas, webClient, requestInterceptor);

        return (T) Proxy.newProxyInstance(target.getClassLoader(), new Class<?>[]{target}, invocationHandler);
    }
}
