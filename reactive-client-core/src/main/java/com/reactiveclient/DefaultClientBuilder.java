package com.reactiveclient;

import com.reactiveclient.client.DefaultWebClientFactory;
import com.reactiveclient.client.codec.ExtendedClientCodecConfigurer;
import com.reactiveclient.handler.DefaultReactiveInvocationHandlerFactory;
import com.reactiveclient.handler.ReactiveInvocationHandlerFactory;
import com.reactiveclient.metadata.MethodMetadata;
import com.reactiveclient.metadata.MethodMetadataFactory;
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
        this.codecs = new ExtendedClientCodecConfigurer();
        this.requestInterceptors = new ArrayList<>();
    }

    @Override
    public ClientBuilder registerDefaultCodecs(boolean registerDefaults) {
        codecs.registerDefaults(registerDefaults);
        return this;
    }

    @Override
    public ClientBuilder errorDecoder(ErrorDecoder errorDecoder) {
        codecs.customCodec().errorDecoder(errorDecoder);
        return this;
    }

    @Override
    public ClientBuilder errorReader(HttpErrorReader httpErrorReader) {
        codecs.customCodec().errorReader(httpErrorReader);
        return this;
    }

    @Override
    public ClientBuilder decoder(Decoder<?> decoder) {
        codecs.customCodec().decoder(decoder);
        return this;
    }

    @Override
    public ClientBuilder messageReader(HttpMessageReader<?> httpMessageReader) {
        codecs.customCodec().reader(httpMessageReader);
        return this;
    }

    @Override
    public ClientBuilder encoder(Encoder<?> encoder) {
        codecs.customCodec().encoder(encoder);
        return this;
    }

    @Override
    public ClientBuilder messageWriter(HttpMessageWriter<?> httpMessageWriter) {
        codecs.customCodec().writer(httpMessageWriter);
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
