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
    private Consumer<Request> requestConsumer;

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

    public ReactiveClientBuilder errorDecoder(ErrorDecoder errorDecoder) {
        this.errorDecoders.add(errorDecoder);
        return this;
    }

    public ReactiveClientBuilder requestConsumer(Iterable<Consumer<Request>> requestConsumers) {
        requestConsumer = request -> {};
        for (Consumer<Request> requestConsumer : requestConsumers) {
            this.requestConsumer = this.requestConsumer.andThen(requestConsumer);
        }
        return this;
    }

    public ReactiveClientBuilder requestConsumer(Consumer<Request> requestConsumer) {
        this.requestConsumer = this.requestConsumer == null ? requestConsumer : this.requestConsumer.andThen(requestConsumer);
        return this;
    }

    public <T> T build(Class<T> target, URI uri) {
        MethodMetadataFactory methodMetadataFactory = new MethodMetadataFactory();
        WebClient webClient = new DefaultWebClientFactory().create(errorDecoders);
        List<MethodMetadata> requestTemplates = methodMetadataFactory.build(target, uri);

        ReactiveInvocationHandlerFactory reactiveInvocationHandlerFactory = new DefaultReactiveInvocationHandlerFactory();
        requestConsumer = requestConsumer != null ? requestConsumer : request -> {};
        return (T) Proxy.newProxyInstance(target.getClassLoader(), new Class<?>[]{target}, reactiveInvocationHandlerFactory.create(requestTemplates, webClient, requestConsumer));
    }
}
