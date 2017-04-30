package com.webfluxclient.client;

import org.springframework.core.ResolvableType;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.webfluxclient.utils.Types.isFlux;
import static com.webfluxclient.utils.Types.isMono;
import static com.webfluxclient.utils.Types.isVoid;

public class DefaultResponseBodyProcessor implements ResponseBodyProcessor {
    @Override
    public Object process(Mono<ClientResponse> monoResponse, ResolvableType bodyType) {
        if (isMono(bodyType)) {
            return toMono(monoResponse, bodyType.getGeneric(0).getRawClass());
        }
        else if (isFlux(bodyType)) {
            return toFlux(monoResponse, bodyType.getGeneric(0).getRawClass());
        }
        else if (isVoid(bodyType)) {
            return toVoid(monoResponse);
        }
        else {
            return toObject(monoResponse, bodyType.getRawClass());
        }
    }
    
    private <T> Mono<T> toMono(Mono<ClientResponse> monoResponse, Class<T> monoContentType) {
        return monoResponse
                .flatMap(clientResponse -> clientResponse.bodyToMono(monoContentType));
    }
    
    private <T> Flux<T> toFlux(Mono<ClientResponse> monoResponse, Class<T> fluxContentType) {
        return monoResponse
                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(fluxContentType));
    }
    
    private <T> T toObject(Mono<ClientResponse> monoResponse, Class<T> responseBodyType) {
        return monoResponse
                .flatMap(clientResponse -> clientResponse.bodyToMono(responseBodyType))
                .block();
    }
    
    private Void toVoid(Mono<ClientResponse> monoResponse) {
        return monoResponse
                .then()
                .block();
    }
}
