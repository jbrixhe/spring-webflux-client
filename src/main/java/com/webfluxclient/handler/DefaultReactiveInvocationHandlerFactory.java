package com.webfluxclient.handler;

import com.webfluxclient.RequestInterceptor;
import com.webfluxclient.client.DefaultWebClientFactory;
import com.webfluxclient.client.RequestProcessor;
import com.webfluxclient.client.RequestProcessors;
import com.webfluxclient.client.WebClientFactory;
import com.webfluxclient.client.codec.ExtendedClientCodecConfigurer;
import com.webfluxclient.metadata.MethodMetadata;
import com.webfluxclient.metadata.MethodMetadataFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultReactiveInvocationHandlerFactory implements ReactiveInvocationHandlerFactory {

    private MethodMetadataFactory methodMetadataFactory;
    private WebClientFactory webClientFactory;

    public DefaultReactiveInvocationHandlerFactory() {
        this.methodMetadataFactory = new MethodMetadataFactory();
        this.webClientFactory = new DefaultWebClientFactory();
    }

    @Override
    public InvocationHandler build(ExtendedClientCodecConfigurer codecConfigurer, RequestInterceptor requestInterceptor, Class<?> target, URI uri) {
        WebClient webClient = webClientFactory.create(codecConfigurer);
        Map<Method, ClientMethodHandler> invocationDispatcher = methodMetadataFactory.build(target, uri)
                .stream()
                .collect(Collectors.toMap(MethodMetadata::getTargetMethod, methodMetadata -> buildReactiveMethodHandler(methodMetadata, webClient, requestInterceptor)));

        return new DefaultReactiveInvocationHandler(invocationDispatcher);
    }

    private ClientMethodHandler buildReactiveMethodHandler(MethodMetadata methodMetadata, WebClient webClient, RequestInterceptor requestInterceptor){
        RequestProcessor requestProcessor = RequestProcessors.defaults(methodMetadata.getRequestBodyType(), methodMetadata.getResponseBodyType());

        return new DefaultClientMethodHandler(webClient, methodMetadata.getRequestTemplate(), requestProcessor, requestInterceptor);
    }
}
