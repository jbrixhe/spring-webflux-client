package com.reactiveclient.handler;

import com.reactiveclient.RequestInterceptor;
import com.reactiveclient.metadata.MethodMetadata;
import com.reactiveclient.metadata.request.Request;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class DefaultReactiveMethodHandler implements ReactiveMethodHandler {

    private WebClient client;
    private MethodMetadata methodMetadata;
    private List<RequestInterceptor> requestInterceptors;
    private Function<Request, Publisher<?>> requestFunction;

    public DefaultReactiveMethodHandler(MethodMetadata methodMetadata, WebClient client, List<RequestInterceptor> requestInterceptors) {
        this.client = client;
        this.methodMetadata = methodMetadata;
        this.requestInterceptors = requestInterceptors;
        this.requestFunction = buildWebClient(methodMetadata)
                .andThen(responseExtractor(methodMetadata.getResponseType()));
    }

    @Override
    public Object invoke(Object[] args) {
        Request request = methodMetadata.getRequestTemplate().apply(args);

        requestInterceptors.forEach(requestInterceptor -> requestInterceptor.accept(request));

        return requestFunction.apply(request);
    }

    private Function<Request, Mono<ClientResponse>> buildWebClient(MethodMetadata methodMetadata) {
        Supplier<UriSpec> uriSpecSupplier = uriSpecSupplier(methodMetadata.getRequestTemplate().getHttpMethod());

        Function<Object, BodyInserter<?, ? super ClientHttpRequest>> objectBodyInserterFunction = bodyInserter(methodMetadata.getBodyType());

        return request -> uriSpecSupplier.get()
                .uri(request.getUri())
                .headers(request.getHttpHeaders())
                .exchange(objectBodyInserterFunction.apply(request.getBody()));
    }

    Supplier<UriSpec> uriSpecSupplier(HttpMethod httpMethod) {
        switch (httpMethod) {
            case GET:
                return client::get;
            case POST:
                return client::post;
            case PUT:
                return client::put;
            default:
                throw new IllegalArgumentException();
        }
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

    Function<Object, BodyInserter<?, ? super ClientHttpRequest>> bodyInserter(Type bodyType) {
        if (bodyType == null) {
            return o -> BodyInserters.empty();
        } else if (ParameterizedType.class.isInstance(bodyType)) {
            ParameterizedType parameterizedType = (ParameterizedType) bodyType;
            Class<?> argumentType = (Class<?>)parameterizedType.getActualTypeArguments()[0];
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            if (Publisher.class.isAssignableFrom(rawType)) {
                return body -> BodyInserters.fromPublisher(Publisher.class.cast(body), argumentType);
            }
        } else {
            return BodyInserters::fromObject;
        }

        throw new IllegalArgumentException();
    }
}
