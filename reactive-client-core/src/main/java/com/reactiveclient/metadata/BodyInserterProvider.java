package com.reactiveclient.metadata;

import org.reactivestreams.Publisher;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;

public interface BodyInserterProvider<T> {
    BodyInserter<T, ReactiveHttpOutputMessage> get(T body);


    class ObjectBodyInserterProvider implements BodyInserterProvider<Object> {

        @Override
        public BodyInserter<Object, ReactiveHttpOutputMessage> get(Object body) {
            return body == null ?
                    BodyInserters.empty():
                    BodyInserters.fromObject(body);
        }
    }

    class PublisherBodyInserterProvider<T> implements BodyInserterProvider<Publisher<T>> {

        private Class<T> bodyType;

        public PublisherBodyInserterProvider(Class<T> bodyType) {
            this.bodyType = bodyType;
        }

        @Override
        public BodyInserter<Publisher<T>, ReactiveHttpOutputMessage> get(Publisher<T> body) {
            return body == null ?
                    BodyInserters.empty():
                    BodyInserters.fromPublisher(body, bodyType);
        }
    }
}