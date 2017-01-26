package com.reactiveclient.metadata;

import org.reactivestreams.Publisher;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;

public interface BodyInserterProvider {
    BodyInserter<Object, ReactiveHttpOutputMessage> get(Object body);


    class ObjectBodyInserterProvider implements BodyInserterProvider {

        @Override
        public BodyInserter<Object, ReactiveHttpOutputMessage> get(Object body) {
            return body == null ?
                    BodyInserters.empty():
                    BodyInserters.fromObject(body);
        }
    }

    class PublisherBodyInserterProvider implements BodyInserterProvider {

        private Class<?> bodyType;

        public PublisherBodyInserterProvider(Class<?> bodyType) {
            this.bodyType = bodyType;
        }

        @Override
        public BodyInserter<Object, ReactiveHttpOutputMessage> get(Object body) {
            return body == null ?
                    BodyInserters.empty():
                    BodyInserters.fromPublisher((Publisher)body, bodyType);
        }
    }
}
