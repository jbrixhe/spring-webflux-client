package com.reactiveclient;

import com.reactiveclient.client.DefaultWebClientFactory;
import com.reactiveclient.handler.DefaultReactiveInvocationHandlerFactory;
import com.reactiveclient.handler.ReactiveInvocationHandlerFactory;
import com.reactiveclient.metadata.MethodMetadata;
import com.reactiveclient.metadata.MethodMetadataFactory;
import com.reactiveclient.metadata.request.Request;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ReactiveClientBuilder {
    private List<ErrorDecoder> errorDecoders;
    private List<RequestInterceptor> requestInterceptors;

    private ReactiveClientBuilder() {
        this.errorDecoders = new ArrayList<>();
        this.requestInterceptors = new ArrayList<>();
    }

    public static ReactiveClientBuilder builder() {
        return new ReactiveClientBuilder();
    }

    public ReactiveClientBuilder errorDecoders(Iterable<ErrorDecoder> errorDecoders) {
        this.errorDecoders.clear();
        for (ErrorDecoder errorDecoder : errorDecoders) {
            this.errorDecoders.add(errorDecoder);
        }
        return this;
    }

    public ReactiveClientBuilder errorDecoder(ErrorDecoder errorDecoder) {
        this.errorDecoders.add(errorDecoder);
        return this;
    }

    public ReactiveClientBuilder requestInterceptors(Iterable<RequestInterceptor> requestInterceptors) {
        this.requestInterceptors.clear();
        for (RequestInterceptor requestConsumer : requestInterceptors) {
            this.requestInterceptors.add(requestConsumer);
        }
        return this;
    }

    public ReactiveClientBuilder requestInterceptor(RequestInterceptor requestConsumer) {
        this.requestInterceptors.add(requestConsumer);
        return this;
    }

    public <T> T build(Class<T> target, String uri) {
        MethodMetadataFactory methodMetadataFactory = new MethodMetadataFactory();
        WebClient webClient = new DefaultWebClientFactory().create(errorDecoders);
        List<MethodMetadata> requestTemplates = methodMetadataFactory.build(target, URI.create(uri));

        ReactiveInvocationHandlerFactory reactiveInvocationHandlerFactory = new DefaultReactiveInvocationHandlerFactory();
        return (T) Proxy.newProxyInstance(target.getClassLoader(), new Class<?>[]{target}, reactiveInvocationHandlerFactory.create(requestTemplates, webClient, requestInterceptors));
    }
}
