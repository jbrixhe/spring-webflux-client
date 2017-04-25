package com.webfluxclient.client;


import org.springframework.core.ResolvableType;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

public class RequestProcessors {

    public static RequestProcessor defaults(ResolvableType requestBodyType,
                                            ResolvableType responseBodyType) {
        return requestProcessorFor(RequestBodyProcessors.forType(requestBodyType),
                                   ResponseBodyProcessors.forType(responseBodyType));
    }

    private static RequestProcessor requestProcessorFor(RequestBodyProcessor requestBodyProcessor,
                                                        ResponseBodyProcessor responseBodyProcessor) {

        return (webClient, request) -> {
            Mono<ClientResponse> clientResponse = webClient.method(request.getHttpMethod())
                    .uri(request.expand())
                    .headers(request.getHttpHeaders())
                    .body(requestBodyProcessor.process(request.getBody()))
                    .exchange();

            return responseBodyProcessor.process(clientResponse);
        };
    }
}
