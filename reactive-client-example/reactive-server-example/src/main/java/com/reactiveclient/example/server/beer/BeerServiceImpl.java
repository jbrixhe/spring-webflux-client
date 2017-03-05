package com.reactiveclient.example.server.beer;

import com.reactiveclient.example.server.DuplicateResourceException;
import com.reactiveclient.example.server.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BeerServiceImpl implements BeerService {

    private Map<String, Beer> beerRepository;

    public BeerServiceImpl() {
        beerRepository = new ConcurrentHashMap<>();
        beerRepository.put("beer1", new Beer("beer1", "Tripel Karmeliet", "Tripel Karmeliet (Dutch for \"Triple Carmelite\") is a golden Belgian beer with high alcohol by volume (8.4%), brewed by Brouwerij Bosteels in Buggenhout, Belgium", new BigDecimal("8.4"), new BigDecimal("4.6")));
        beerRepository.put("beer2", new Beer("beer2", "Spencer Trappist Ale", "The only Trappist beer in America", new BigDecimal("6.5"), new BigDecimal("3.8")));
        beerRepository.put("beer3", new Beer("beer3", "Chouffe", "", new BigDecimal("8.0"), new BigDecimal("4.1")));
    }

    @Override
    public Mono<Beer> getBeer(String code) {
        return beerRepository.containsKey(code) ?
                Mono.just(beerRepository.get(code)) :
                Mono.error(new ResourceNotFoundException("Beer not found for code: " + code));
    }

    @Override
    public Flux<Beer> getBeers() {
        return Flux.fromIterable(beerRepository.values());
    }

    @Override
    public Mono<Beer> addBeer(Mono<Beer> newBeer) {
        return newBeer
                .filter(beerRequest -> beerRepository.containsKey(beerRequest.getName()))
                .map(beer -> beerRepository.put(beer.getCode(), beer))
                .otherwiseIfEmpty(Mono.error(new DuplicateResourceException()));
    }

    @Override
    public Mono<Beer> updateBeer(String beerCode, Mono<Beer> beerToUpdate) {
        return beerToUpdate
                .and(Mono.just(beerRepository.get(beerCode)), (beer, target) -> {
                    target.setName(beer.getName());
                    target.setDescription(beer.getDescription());
                    target.setStars(beer.getStars());
                    target.setAbv(beer.getAbv());
                    return target;
                });
    }

}
