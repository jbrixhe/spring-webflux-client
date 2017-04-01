package com.reactiveclient.handler;

import com.reactiveclient.RequestInterceptor;
import com.reactiveclient.metadata.MethodMetadata;
import com.reactiveclient.metadata.request.ClientRequest;
import org.reactivestreams.Publisher;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DefaultReactiveMethodHandler implements ReactiveMethodHandler {

    private WebClient client;
    private MethodMetadata methodMetadata;
    private List<RequestInterceptor> requestInterceptors;
    private Function<ClientRequest, Publisher<?>> requestFunction;

    public DefaultReactiveMethodHandler(MethodMetadata methodMetadata, WebClient client, List<RequestInterceptor> requestInterceptors) {
        this.client = client;
        this.methodMetadata = methodMetadata;
        this.requestInterceptors = requestInterceptors;
        this.requestFunction = buildWebClient(methodMetadata)
                .andThen(responseExtractor(methodMetadata.getResponseType()));
    }

    @Override
    public Object invoke(Object[] args) {
        ClientRequest clientRequest = methodMetadata.getRequestTemplate().apply(args);

        requestInterceptors.forEach(requestInterceptor -> requestInterceptor.accept(clientRequest));

        return requestFunction.apply(clientRequest);
    }

    private Function<ClientRequest, Mono<ClientResponse>> buildWebClient(MethodMetadata methodMetadata) {

        Function<ClientRequest, BodyInserter<?, ? super ClientHttpRequest>> clientRequestBodyInserterFunction = bodyInserter(methodMetadata.getBodyType());

        return request -> client.method(request.getHttpMethod())
                .uri(request.expand())
                .headers(request.getHttpHeaders())
                .body(clientRequestBodyInserterFunction.apply(request))
                .exchange();
    }

    Function<Mono<ClientResponse>, Publisher<?>> responseExtractor(Type returnType) {
        if (ParameterizedType.class.isInstance(returnType)) {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            Class<?> argumentType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();

            if (Mono.class.isAssignableFrom(rawType)) {
                return clientResponseMono -> clientResponseMono.then(clientResponse -> clientResponse.bodyToMono(argumentType));
            } else if (Flux.class.isAssignableFrom(rawType)) {
                return clientResponseMono -> clientResponseMono.flatMap(clientResponse -> clientResponse.bodyToFlux(argumentType));
            }
        } else if (void.class.equals(returnType)) {
            return Mono::then;
        }

        throw new IllegalArgumentException();
    }

    Function<ClientRequest, BodyInserter<?, ? super ClientHttpRequest>> bodyInserter(Type bodyType) {
        if (bodyType == null) {
            return o -> BodyInserters.empty();
        } else if (ParameterizedType.class.isInstance(bodyType)) {
            ParameterizedType parameterizedType = (ParameterizedType) bodyType;
            Class<?> argumentType = (Class<?>)parameterizedType.getActualTypeArguments()[0];
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();

            if (Publisher.class.isAssignableFrom(rawType)) {
                return clientRequest -> BodyInserters.fromPublisher(Publisher.class.cast(clientRequest.getBody()), argumentType);
            }
        }

        return clientRequest -> BodyInserters.fromObject(clientRequest.getBody());
    }
}
