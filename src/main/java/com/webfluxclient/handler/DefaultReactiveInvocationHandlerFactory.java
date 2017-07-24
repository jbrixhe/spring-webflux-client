package com.webfluxclient.handler;

import com.webfluxclient.LogLevel;
import com.webfluxclient.Logger;
import com.webfluxclient.RequestProcessor;
import com.webfluxclient.ResponseProcessor;
import com.webfluxclient.client.DefaultExchangeFilterFunctionFactory;
import com.webfluxclient.client.DefaultRequestExecutorFactory;
import com.webfluxclient.client.DefaultResponseBodyProcessor;
import com.webfluxclient.client.ExchangeFilterFunctionFactory;
import com.webfluxclient.client.RequestExecutor;
import com.webfluxclient.client.RequestExecutorFactory;
import com.webfluxclient.client.ResponseBodyProcessor;
import com.webfluxclient.codec.ExtendedClientCodecConfigurer;
import com.webfluxclient.metadata.MethodMetadata;
import com.webfluxclient.metadata.MethodMetadataFactory;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class DefaultReactiveInvocationHandlerFactory implements ReactiveInvocationHandlerFactory {

    private MethodMetadataFactory methodMetadataFactory;
    private RequestExecutorFactory requestExecutorFactory;
    private ExchangeFilterFunctionFactory exchangeFilterFunctionFactory;

    public DefaultReactiveInvocationHandlerFactory() {
        this.methodMetadataFactory = new MethodMetadataFactory();
        this.requestExecutorFactory = new DefaultRequestExecutorFactory();
        this.exchangeFilterFunctionFactory = new DefaultExchangeFilterFunctionFactory();
    }

    @Override
    public InvocationHandler build(ExtendedClientCodecConfigurer codecConfigurer, List<RequestProcessor> requestProcessors, List<ResponseProcessor> responseProcessors, Logger logger, LogLevel logLevel, Class<?> target, URI uri) {
        ExchangeFilterFunction exchangeFilterFunction = exchangeFilterFunctionFactory.build(requestProcessors, responseProcessors, logger, logLevel);
        RequestExecutor requestExecutor = requestExecutorFactory.build(codecConfigurer, exchangeFilterFunction);
        ResponseBodyProcessor responseBodyProcessor = new DefaultResponseBodyProcessor(codecConfigurer.getErrorReaders());

        Map<Method, ClientMethodHandler> invocationDispatcher = methodMetadataFactory.build(target, uri)
                .stream()
                .collect(toMap(MethodMetadata::getTargetMethod, methodMetadata -> new DefaultClientMethodHandler(methodMetadata, requestExecutor, responseBodyProcessor)));

        return new DefaultReactiveInvocationHandler(invocationDispatcher);
    }
}
