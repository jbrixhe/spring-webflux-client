package com.reactiveclient.core.example;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping(path = "/beers")
public interface BeerClient {

    @GetMapping(path = "/{code}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    Mono<Beer> getBeer(@PathVariable("code") String code);

    @GetMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    Flux<Beer> getMessages();

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    Mono<Void> addBeer(@RequestBody Mono<Beer> newBeer);

    @PutMapping(path = "/{code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    Mono<Beer> updateMessage(@PathVariable("code") String code, Mono<Beer> newMessage);

}