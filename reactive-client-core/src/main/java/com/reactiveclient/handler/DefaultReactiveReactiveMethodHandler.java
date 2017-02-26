package com.reactiveclient.handler;

import com.reactiveclient.metadata.MethodMetadata;
import com.reactiveclient.metadata.request.Request;
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
import java.util.function.Consumer;
import java.util.function.Function;

public class DefaultReactiveReactiveMethodHandler implements ReactiveMethodHandler {

    private MethodMetadata methodMetadata;
    private Function<Mono<ClientResponse>, Publisher<?>> responseExtractor;
    private Function<Object, BodyInserter<?, ? super ClientHttpRequest>> bodyInserterFunction;
    private Consumer<Request> requestInterceptor;
    private WebClient client;

    public DefaultReactiveReactiveMethodHandler(MethodMetadata methodMetadata, WebClient client, Consumer<Request> requestInterceptor) {
        this.client = client;
        this.methodMetadata = methodMetadata;
        this.requestInterceptor = requestInterceptor;
        this.responseExtractor = responseExtractor(methodMetadata.getResponseType());
        this.bodyInserterFunction = bodyInserter(methodMetadata.getBodyType());
    }

    @Override
    public Object invoke(Object[] args) {
        Request request = methodMetadata.getRequestTemplate().apply(args);

        requestInterceptor.accept(request);

        return responseExtractor.apply(buildWebClient(request));
    }

    private Mono<ClientResponse> buildWebClient(Request request) {
        switch (request.getHttpMethod()) {
            case GET:
                return client.get()
                        .uri(request.getUri())
                        .headers(request.getHttpHeaders())
                        .exchange();
            case POST:
                return client.post()
                        .uri(request.getUri())
                        .headers(request.getHttpHeaders())
                        .exchange(bodyInserterFunction.apply(request.getBody()));
            case PUT:
                return client.put()
                        .uri(request.getUri())
                        .headers(request.getHttpHeaders())
                        .exchange(bodyInserterFunction.apply(request.getBody()));
            default:
                throw new RuntimeException();
        }
    }


    Function<Mono<ClientResponse>, Publisher<?>> responseExtractor(Type returnType) {
        if (ParameterizedType.class.isInstance(returnType)) {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            Type argumentType = parameterizedType.getActualTypeArguments()[0];
            if (ParameterizedType.class.isInstance(argumentType)) {
                throw new IllegalArgumentException("Embedded generic type not supported yet.");
            }

            if (Mono.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
                return clientResponseMono -> clientResponseMono.then(clientResponse -> clientResponse.bodyToMono((Class<?>) argumentType));
            } else if (Flux.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
                return clientResponseMono -> clientResponseMono.flatMap(clientResponse -> clientResponse.bodyToFlux((Class<?>) argumentType));
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
            Type argumentType = parameterizedType.getActualTypeArguments()[0];
            if (ParameterizedType.class.isInstance(argumentType)) {
                throw new IllegalArgumentException("Embedded generic type not supported yet.");
            }

            if (Publisher.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
                return body -> BodyInserters.fromPublisher((Publisher) body, (Class<?>) argumentType);
            }
        } else {
            return BodyInserters::fromObject;
        }

        throw new IllegalArgumentException();
    }
}
