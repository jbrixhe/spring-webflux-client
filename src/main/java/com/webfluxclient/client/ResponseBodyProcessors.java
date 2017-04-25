package com.webfluxclient.client;

import org.springframework.core.ResolvableType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.webfluxclient.utils.Types.*;

class ResponseBodyProcessors {

    static ResponseBodyProcessor forType(ResolvableType responseBodyType) {
        if (isMono(responseBodyType)) {
            return forMono(responseBodyType.getGeneric(0));
        } else if (isFlux(responseBodyType)) {
            return forFlux(responseBodyType.getGeneric(0));
        } else if (isVoid(responseBodyType)) {
            return forVoid();
        } else {
            return forObject(responseBodyType);
        }
    }

    static ResponseBodyProcessor<Mono> forMono(ResolvableType monoContentType) {
        return clientResponseMono -> clientResponseMono
                .flatMap(clientResponse -> clientResponse
                        .bodyToMono(monoContentType.getRawClass()));
    }

    static ResponseBodyProcessor<Flux> forFlux(ResolvableType fluxContentType) {
        return clientResponseMono -> clientResponseMono
                .flatMapMany(clientResponse -> clientResponse
                        .bodyToFlux(fluxContentType.getRawClass()));
    }

    static ResponseBodyProcessor<Object> forObject(ResolvableType fluxContentType) {
        return clientResponseMono -> clientResponseMono
                .map(clientResponse -> clientResponse
                        .bodyToMono(fluxContentType.getRawClass())
                        .block());
    }

    static ResponseBodyProcessor<Void> forVoid() {
        return clientResponse -> null;
    }
}
