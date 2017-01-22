package com.reactiveclient.test;

import com.reactiveclient.ReactiveClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ReactiveClient(url = "http://localhost:8080")
public interface SimpleReactiveClient {

    @RequestMapping(path = "/hello")
    Mono<ReactiveClientTests.Hello> getHello();

    @RequestMapping(path = "/hellos", consumes = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ReactiveClientTests.Hello> getHellos();
}
