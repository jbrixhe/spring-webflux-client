package com.reactiveclient.core.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
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
        Beer beer = beerClient.getBeer("beer1")
                .doOnError(throwable -> System.out.println(throwable.getClass().getName()))
                .block();

        System.out.println(beer);
    }
}
