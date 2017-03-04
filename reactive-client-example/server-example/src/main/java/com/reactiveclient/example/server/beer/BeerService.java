package com.reactiveclient.example.server.beer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BeerService {
    Mono<Beer> getBeer(String code);

    Flux<Beer> getBeers();

    Mono<Beer> addBeer(Mono<Beer> newBeer);

    Mono<Beer> updateBeer(String beerCode, Mono<Beer> beerToUpdate);
}
