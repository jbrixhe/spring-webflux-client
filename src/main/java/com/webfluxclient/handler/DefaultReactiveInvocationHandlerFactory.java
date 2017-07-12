package com.webfluxclient.handler;

import com.webfluxclient.RequestProcessor;
import com.webfluxclient.ResponseInterceptor;
import com.webfluxclient.client.DefaultRequestExecutorFactory;
import com.webfluxclient.client.DefaultResponseBodyProcessor;
import com.webfluxclient.client.RequestExecutor;
import com.webfluxclient.client.RequestExecutorFactory;
import com.webfluxclient.client.ResponseBodyProcessor;
import com.webfluxclient.codec.ExtendedClientCodecConfigurer;
import com.webfluxclient.metadata.MethodMetadata;
import com.webfluxclient.metadata.MethodMetadataFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class DefaultReactiveInvocationHandlerFactory implements ReactiveInvocationHandlerFactory {

    private MethodMetadataFactory methodMetadataFactory;
    private RequestExecutorFactory requestExecutorFactory;

    public DefaultReactiveInvocationHandlerFactory() {
        this.methodMetadataFactory = new MethodMetadataFactory();
        this.requestExecutorFactory = new DefaultRequestExecutorFactory();
    }

    @Override
    public InvocationHandler build(ExtendedClientCodecConfigurer codecConfigurer, List<RequestProcessor> requestProcessors, List<ResponseInterceptor> responseInterceptors, Class<?> target, URI uri) {
        RequestExecutor requestExecutor = requestExecutorFactory.build(codecConfigurer, requestProcessors, responseInterceptors);
        ResponseBodyProcessor responseBodyProcessor = new DefaultResponseBodyProcessor(codecConfigurer.getErrorReaders());

        Map<Method, ClientMethodHandler> invocationDispatcher = methodMetadataFactory.build(target, uri)
                .stream()
                .collect(toMap(MethodMetadata::getTargetMethod, methodMetadata -> new DefaultClientMethodHandler(methodMetadata, requestExecutor, responseBodyProcessor)));

        return new DefaultReactiveInvocationHandler(invocationDispatcher);
    }
}
