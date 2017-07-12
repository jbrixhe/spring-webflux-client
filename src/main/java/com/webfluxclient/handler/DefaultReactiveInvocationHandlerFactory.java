package com.webfluxclient.handler;

import com.webfluxclient.RequestInterceptor;
import com.webfluxclient.client.DefaultRequestExecutorFactory;
import com.webfluxclient.client.DefaultResponseBodyProcessorFactory;
import com.webfluxclient.client.RequestExecutor;
import com.webfluxclient.client.RequestExecutorFactory;
import com.webfluxclient.client.ResponseBodyProcessor;
import com.webfluxclient.client.ResponseBodyProcessorFactory;
import com.webfluxclient.codec.ExtendedClientCodecConfigurer;
import com.webfluxclient.metadata.MethodMetadata;
import com.webfluxclient.metadata.MethodMetadataFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultReactiveInvocationHandlerFactory implements ReactiveInvocationHandlerFactory {

    private MethodMetadataFactory methodMetadataFactory;
    private RequestExecutorFactory requestExecutorFactory;
    private ResponseBodyProcessorFactory responseBodyProcessorFactory;

    public DefaultReactiveInvocationHandlerFactory() {
        this.methodMetadataFactory = new MethodMetadataFactory();
        this.requestExecutorFactory = new DefaultRequestExecutorFactory();
        this.responseBodyProcessorFactory = new DefaultResponseBodyProcessorFactory();
    }

    @Override
    public InvocationHandler build(ExtendedClientCodecConfigurer codecConfigurer, List<RequestInterceptor> requestInterceptors, Class<?> target, URI uri) {
        RequestInterceptor requestInterceptor = requestInterceptors.stream()
                .reduce(RequestInterceptor::andThen)
                .orElse(reactiveRequest ->{});

        ResponseBodyProcessor responseBodyProcessor = responseBodyProcessorFactory.create(codecConfigurer);
        RequestExecutor requestExecutor = requestExecutorFactory.create(codecConfigurer);
        Map<Method, ClientMethodHandler> invocationDispatcher = methodMetadataFactory.build(target, uri)
                .stream()
                .collect(Collectors.toMap(MethodMetadata::getTargetMethod, methodMetadata -> new DefaultClientMethodHandler(methodMetadata, requestExecutor, requestInterceptor, responseBodyProcessor)));

        return new DefaultReactiveInvocationHandler(invocationDispatcher);
    }
}
