package com.reactiveclient.starter.test;

import com.reactiveclient.starter.ReactiveClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@ReactiveClient(url = "http://localhost:8080")
public interface HelloClient {
    @RequestMapping(method = RequestMethod.GET, path = "/hello")
    Mono<ReactiveClientTests.Hello> getHello();

    @RequestMapping(method = RequestMethod.GET, path = "/hellos", consumes = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ReactiveClientTests.Hello> getHellos();

    @RequestMapping(method = RequestMethod.GET, path = "/string")
    Mono<String> getString(@RequestParam("name") List<String> names);
}
