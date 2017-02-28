package com.reactiveclient.example.client;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HelloClient {

    @RequestMapping(method = RequestMethod.GET, path = "/hello")
    Mono<Hello> getHello();

    @RequestMapping(method = RequestMethod.GET, path = "/hellos", consumes = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Hello> getHellos();

}