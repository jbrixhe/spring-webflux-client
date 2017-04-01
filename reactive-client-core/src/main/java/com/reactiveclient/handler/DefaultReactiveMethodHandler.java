package com.reactiveclient.handler;

import com.reactiveclient.RequestInterceptor;
import com.reactiveclient.metadata.MethodMetadata;
import com.reactiveclient.metadata.request.ReactiveRequest;
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
import java.util.function.Function;

public class DefaultReactiveMethodHandler implements ReactiveMethodHandler {

    private WebClient client;
    private MethodMetadata methodMetadata;
    private List<RequestInterceptor> requestInterceptors;
    private Function<ReactiveRequest, Publisher<?>> requestFunction;

    public DefaultReactiveMethodHandler(MethodMetadata methodMetadata, WebClient client, List<RequestInterceptor> requestInterceptors) {
        this.client = client;
        this.methodMetadata = methodMetadata;
        this.requestInterceptors = requestInterceptors;
        this.requestFunction = buildWebClient(methodMetadata)
                .andThen(responseExtractor(methodMetadata.getResponseType()));
    }

    @Override
    public Object invoke(Object[] args) {
        ReactiveRequest reactiveRequest = methodMetadata.getReactiveRequestTemplate().apply(args);

        requestInterceptors.forEach(requestInterceptor -> requestInterceptor.accept(reactiveRequest));

        return requestFunction.apply(reactiveRequest);
    }

    private Function<ReactiveRequest, Mono<ClientResponse>> buildWebClient(MethodMetadata methodMetadata) {

        Function<ReactiveRequest, BodyInserter<?, ? super ClientHttpRequest>> clientRequestBodyInserterFunction = bodyInserter(methodMetadata.getBodyType());

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

    Function<ReactiveRequest, BodyInserter<?, ? super ClientHttpRequest>> bodyInserter(Type bodyType) {
        if (bodyType == null) {
            return o -> BodyInserters.empty();
        } else if (ParameterizedType.class.isInstance(bodyType)) {
            ParameterizedType parameterizedType = (ParameterizedType) bodyType;
            Class<?> argumentType = (Class<?>)parameterizedType.getActualTypeArguments()[0];
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();

            if (Publisher.class.isAssignableFrom(rawType)) {
                return reactiveRequest -> BodyInserters.fromPublisher(Publisher.class.cast(reactiveRequest.getBody()), argumentType);
            }
        }

        return reactiveRequest -> BodyInserters.fromObject(reactiveRequest.getBody());
    }
}
