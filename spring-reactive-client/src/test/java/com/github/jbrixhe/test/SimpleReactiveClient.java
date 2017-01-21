package com.github.jbrixhe.test;

import com.github.jbrixhe.reactiveclient.ReactiveClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ReactiveClient
@RequestMapping(path = "/api/user")
public interface SimpleReactiveClient {

    @RequestMapping(path = "/{id}")
    Flux<String> getUser(@PathVariable("id") Integer userId);

    @RequestMapping(path = "/{id}/address")
    Mono<String> getUserAddress(@PathVariable("id") Integer userId, @RequestParam("addressType") String addressType);

    void test();
}
