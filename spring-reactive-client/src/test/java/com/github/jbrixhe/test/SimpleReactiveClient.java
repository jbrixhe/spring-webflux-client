package com.github.jbrixhe.test;

import com.github.jbrixhe.reactiveclient.ReactiveClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@ReactiveClient(url = "http://localhost:8080")
public interface SimpleReactiveClient {

    @RequestMapping(path = "/hello")
    Mono<ReactiveClientTests.Hello> getHello();

    @RequestMapping(path = "/hellos", consumes = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ReactiveClientTests.Hello> getHellos();
}
