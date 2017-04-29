package com.webfluxclient.handler;

import com.webfluxclient.RequestInterceptor;
import com.webfluxclient.client.DefaultResponseBodyProcessor;
import com.webfluxclient.client.RequestExecutor;
import com.webfluxclient.client.ResponseBodyProcessor;
import com.webfluxclient.metadata.MethodMetadata;
import com.webfluxclient.metadata.request.Request;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

public class DefaultClientMethodHandler implements ClientMethodHandler {

    private MethodMetadata methodMetadata;
    private RequestInterceptor requestInterceptor;
    private RequestExecutor requestExecutor;
    private ResponseBodyProcessor responseBodyProcessor;
    DefaultClientMethodHandler(MethodMetadata methodMetadata, RequestExecutor requestExecutor, RequestInterceptor requestInterceptor) {
        this.methodMetadata = methodMetadata;
        this.requestExecutor = requestExecutor;
        this.requestInterceptor = requestInterceptor;
        this.responseBodyProcessor = new DefaultResponseBodyProcessor();
    }

    @Override
    public Object invoke(Object[] args) {
        Request request = methodMetadata.getRequestTemplate().apply(args);

        requestInterceptor.accept(request);
    
        Mono<ClientResponse> execute = requestExecutor.execute(request);
    
        return responseBodyProcessor.process(execute, methodMetadata.getResponseBodyType());
    }

}
