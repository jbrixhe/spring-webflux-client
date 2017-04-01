package com.reactiveclient.core.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.net.URISyntaxException;

@SpringBootApplication
public class ClientExampleApplication {

    @Autowired
    private BeerClient beerClient;

    public static void main(String[] args) {
        SpringApplication.run(ClientExampleApplication.class, args);
    }

    @PostConstruct
    public void init() throws URISyntaxException {
        Beer newBeer = new Beer("beer4", "Chimay Bleu", "Quadruple brune", new BigDecimal("8.5"), new BigDecimal("4.3"));
        beerClient.addBeer(Mono.just(newBeer))
                .doOnError(throwable -> System.out.println(throwable.getClass().getName()))
                .block();

        System.out.println("added");
    }
}
