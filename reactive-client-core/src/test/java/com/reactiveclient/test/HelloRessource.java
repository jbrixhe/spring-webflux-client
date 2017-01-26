package com.reactiveclient.test;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HelloRessource {

    @RequestMapping(method = RequestMethod.GET, path = "/hello")
    Mono<ReactiveClientTests.Hello> getHello();

    @RequestMapping(method = RequestMethod.GET, path = "/hellos", consumes = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ReactiveClientTests.Hello> getHellos();

    @RequestMapping(method = RequestMethod.POST, path = "/hello", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Mono<ReactiveClientTests.Hello> createHello(@RequestBody ReactiveClientTests.Hello hello);

    @RequestMapping(method = RequestMethod.POST, path = "/hello/async", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Mono<ReactiveClientTests.Hello> asyncCreateHello(@RequestBody ReactiveClientTests.Hello hello);

    @RequestMapping(method = RequestMethod.PUT, path = "/hello", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Mono<ReactiveClientTests.Hello> updateHello(@RequestBody ReactiveClientTests.Hello hello);
}
