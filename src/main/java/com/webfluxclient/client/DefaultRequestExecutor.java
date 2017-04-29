package com.webfluxclient.client;

import com.webfluxclient.metadata.request.Request;
import lombok.AllArgsConstructor;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class DefaultRequestExecutor implements RequestExecutor {
    private WebClient webClient;
    
    @Override
    public Mono<ClientResponse> execute(Request request) {
        return webClient
                .method(request.getHttpMethod())
                .uri(request.expand())
                .headers(request.getHttpHeaders())
                .body(request.getBodyInserter())
                .exchange();
    }
    
}
