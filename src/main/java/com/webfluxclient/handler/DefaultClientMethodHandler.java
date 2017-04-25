package com.webfluxclient.handler;

import com.webfluxclient.RequestInterceptor;
import com.webfluxclient.client.RequestProcessor;
import com.webfluxclient.metadata.MethodMetadata;
import com.webfluxclient.metadata.request.Request;
import com.webfluxclient.metadata.request.RequestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

public class DefaultClientMethodHandler implements ClientMethodHandler {

    private WebClient webClient;
    private RequestTemplate requestTemplate;
    private RequestInterceptor requestInterceptor;
    private RequestProcessor requestProcessor;

    DefaultClientMethodHandler(
            WebClient webClient,
            RequestTemplate requestTemplate,
            RequestProcessor requestProcessor,
            RequestInterceptor requestInterceptor) {
        this.webClient = webClient;
        this.requestTemplate = requestTemplate;
        this.requestInterceptor = requestInterceptor;
        this.requestProcessor = requestProcessor;
    }

    @Override
    public Object invoke(Object[] args) {
        Request request = requestTemplate.apply(args);

        requestInterceptor.accept(request);

        return requestProcessor.execute(webClient, request);
    }

}
