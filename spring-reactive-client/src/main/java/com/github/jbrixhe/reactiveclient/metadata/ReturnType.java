package com.github.jbrixhe.reactiveclient.metadata;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReturnType {
    private ResponseExtractor responseExtractor;
    private Class<?> targetClass;

    public Object convert(Mono<ClientResponse> clientResponse){
        return responseExtractor.apply(clientResponse, targetClass);
    }

    public static ReturnType monoOf(Class<?> targetClass) {
        return new ReturnType(ResponseExtractor.MONO, targetClass);
    }

    public static ReturnType fluxOf(Class<?> targetClass) {
        return new ReturnType(ResponseExtractor.FLUX, targetClass);
    }

    public static ReturnType none() {
        return new ReturnType(ResponseExtractor.VOID, Void.class);
    }

    private enum ResponseExtractor implements BiFunction<Mono<ClientResponse>, Class<?>, Object> {
        MONO {
            @Override
            public Object apply(Mono<ClientResponse> clientResponse, Class<?> aClass) {
                return clientResponse.then(clientResponse1 -> clientResponse1.bodyToMono(aClass));
            }
        },
        FLUX {
            @Override
            public Object apply(Mono<ClientResponse> clientResponse, Class<?> aClass) {
                return clientResponse.flatMap(clientResponse1 -> clientResponse1.bodyToFlux(aClass));
            }
        },
        VOID {
            @Override
            public Object apply(Mono<ClientResponse> clientResponseMono, Class<?> aClass) {
                return null;
            }
        }
    }
}
