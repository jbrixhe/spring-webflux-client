package com.reactiveclient;

import com.reactiveclient.client.DefaultWebClientFactory;
import com.reactiveclient.client.codec.ExtendedClientCodecConfigurer;
import com.reactiveclient.handler.DefaultReactiveInvocationHandlerFactory;
import com.reactiveclient.handler.ReactiveInvocationHandlerFactory;
import com.reactiveclient.metadata.MethodMetadata;
import com.reactiveclient.metadata.MethodMetadataFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *  A mutable builder to configure a Proxy
 *
 * @author Jérémy Brixhe
 * */
public class ReactiveClientBuilder {
    private MethodMetadataFactory methodMetadataFactory = new MethodMetadataFactory();
    private ReactiveInvocationHandlerFactory reactiveInvocationHandlerFactory = new DefaultReactiveInvocationHandlerFactory();
    private DefaultWebClientFactory defaultWebClientFactory = new DefaultWebClientFactory();
    private ExtendedClientCodecConfigurer codecs;
    private List<RequestInterceptor> requestInterceptors;

    private ReactiveClientBuilder() {
        this.codecs = new ExtendedClientCodecConfigurer();
        this.requestInterceptors = new ArrayList<>();
    }

    /**
     * Return a mutable builder with the default initialization.
     */
    public static ReactiveClientBuilder builder() {
        return new ReactiveClientBuilder();
    }

    /**
     * Convenient method to create a new proxy with default initialization.
     *
     * @param target The interface class to initialize the proxy.
     * @param uri The base URI for all request
     * */
    public static <T> T create(Class<T> target, URI uri) {
        return new ReactiveClientBuilder()
                .build(target, uri);
    }

    /**
     * Add the given {@link ErrorDecoder} to this builder. This is a convenient alternative to adding a
     * {@link DecoderHttpErrorReader} that wraps the given decoder.
     * @param errorDecoder the decoder to add
     * @return this builder
     */
    public ReactiveClientBuilder errorDecoder(ErrorDecoder errorDecoder) {
        this.codecs.customCodec().errorDecoder(errorDecoder);
        return this;
    }

    public ReactiveClientBuilder errorDecoders(Iterable<ErrorDecoder> errorDecoders) {
        for (ErrorDecoder errorDecoder : errorDecoders) {
            errorDecoder(errorDecoder);
        }
        return this;
    }

    /**
     * Add a {@link Consumer} to the builder.
     *
     * @param requestInterceptor The request consumer to use.
     * @return this builder
     * */
    public ReactiveClientBuilder requestInterceptor(RequestInterceptor requestInterceptor) {
        this.requestInterceptors.add(requestInterceptor);
        return this;
    }

    public ReactiveClientBuilder requestInterceptors(Iterable<RequestInterceptor> requestInterceptors) {
        for (RequestInterceptor requestInterceptor : requestInterceptors) {
            this.requestInterceptors.add(requestInterceptor);
        }
        return this;
    }

    /**
     * Build the proxy instance
     *
     * @param target The interface class to initialize the new proxy.
     * @param uri The base Uri for all request
     * @return a configured Porxy for the target class
     * */
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
