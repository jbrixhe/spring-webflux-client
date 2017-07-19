package com.webfluxclient.client;

import com.webfluxclient.codec.HttpErrorReader;
import lombok.AllArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.webfluxclient.utils.Types.isFlux;
import static com.webfluxclient.utils.Types.isMono;
import static com.webfluxclient.utils.Types.isVoid;

@AllArgsConstructor
public class DefaultResponseBodyProcessor implements ResponseBodyProcessor {
    private List<HttpErrorReader> httpErrorReaders;

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
                .flatMap(response -> bodyToPublisher(response,
                                BodyExtractors.toMono(monoContentType),
                                ErrorBodyExtractors.toMono(httpErrorReaders)));
    }
    
    private <T> Flux<T> toFlux(Mono<ClientResponse> monoResponse, Class<T> fluxContentType) {
        return monoResponse
                .flatMapMany(response -> bodyToPublisher(response,
                                BodyExtractors.toFlux(fluxContentType),
                                ErrorBodyExtractors.toFlux(httpErrorReaders)));
    }
    
    private <T> T toObject(Mono<ClientResponse> monoResponse, Class<T> responseBodyType) {
        return toMono(monoResponse, responseBodyType)
                .block();
    }
    
    private Void toVoid(Mono<ClientResponse> monoResponse) {
        return monoResponse
                .then()
                .block();
    }

    private <T extends Publisher<?>> T bodyToPublisher(ClientResponse response,
                                                       BodyExtractor<T, ? super ClientHttpResponse> bodyExtractor,
                                                       BodyExtractor<T, ? super ClientHttpResponse> errorBodyExtractor) {
        return response.statusCode().isError() ?
                response.body(errorBodyExtractor) :
                response.body(bodyExtractor);
    }
}
