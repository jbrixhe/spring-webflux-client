package com.webfluxclient.client;


import org.springframework.core.ResolvableType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class ResponseBodyProcessors {

    static ResponseBodyProcessor forType(ResolvableType responseBodyType) {
        if (Mono.class.isAssignableFrom(responseBodyType.getRawClass())) {
            return forMono(responseBodyType.getGeneric(0));
        } else if (Flux.class.isAssignableFrom(responseBodyType.getRawClass())) {
            return forFlux(responseBodyType.getGeneric(0));
        } else if(void.class.isAssignableFrom(responseBodyType.getRawClass())) {
            return forVoid();
        } else {
            return forObject(responseBodyType);
        }
    }

    static ResponseBodyProcessor forMono(ResolvableType monoContentType) {
        return clientResponseMono -> clientResponseMono
                .flatMap(clientResponse -> clientResponse
                        .bodyToMono(monoContentType.getRawClass()));
    }

    static ResponseBodyProcessor forFlux(ResolvableType fluxContentType) {
        return clientResponseMono -> clientResponseMono
                .flatMapMany(clientResponse -> clientResponse
                        .bodyToFlux(fluxContentType.getRawClass()));
    }

    static ResponseBodyProcessor forObject(ResolvableType fluxContentType) {
        return clientResponseMono -> clientResponseMono
                .map(clientResponse -> clientResponse
                        .bodyToMono(fluxContentType.getRawClass())
                        .block());
    }

    static ResponseBodyProcessor forVoid() {
        return clientResponse -> null;
    }
}
