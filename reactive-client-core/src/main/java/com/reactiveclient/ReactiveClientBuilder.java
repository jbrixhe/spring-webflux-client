package com.reactiveclient;

import com.reactiveclient.client.DefaultWebClientFactory;
import com.reactiveclient.handler.DefaultReactiveInvocationHandlerFactory;
import com.reactiveclient.handler.ReactiveInvocationHandlerFactory;
import com.reactiveclient.metadata.MethodMetadata;
import com.reactiveclient.metadata.MethodMetadataFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ReactiveClientBuilder {
    private List<ErrorDecoder> errorDecoders;

    private ReactiveClientBuilder() {
        this.errorDecoders = new ArrayList<>();
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

    public ReactiveClientBuilder errorDecoders(ErrorDecoder... errorDecoders) {
        this.errorDecoders.clear();
        for (ErrorDecoder errorDecoder : errorDecoders) {
            this.errorDecoders.add(errorDecoder);
        }
        return this;
    }

    public <T> T build(Class<T> target, URI uri) {
        MethodMetadataFactory methodMetadataFactory = new MethodMetadataFactory();
        WebClient webClient = new DefaultWebClientFactory().create(errorDecoders);
        List<MethodMetadata> requestTemplates = methodMetadataFactory.build(target, uri);

        ReactiveInvocationHandlerFactory reactiveInvocationHandlerFactory = new DefaultReactiveInvocationHandlerFactory();

        return (T) Proxy.newProxyInstance(target.getClassLoader(), new Class<?>[]{target}, reactiveInvocationHandlerFactory.create(requestTemplates, webClient));
    }
}
