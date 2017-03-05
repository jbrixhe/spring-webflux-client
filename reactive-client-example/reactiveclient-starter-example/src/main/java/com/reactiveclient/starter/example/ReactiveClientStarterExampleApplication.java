package com.reactiveclient.starter.example;

import com.reactiveclient.starter.EnableReactiveClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@EnableReactiveClient
@SpringBootApplication
public class ReactiveClientStarterExampleApplication {

    @Autowired
    private BeerClient beerClient;

    public static void main(String[] args) {
        SpringApplication.run(ReactiveClientStarterExampleApplication.class, args);
    }

    @PostConstruct
    public void init() {
        Beer beer1 = beerClient.getBeer("beer1")
                .block();
        System.out.println(beer1);
    }
}
