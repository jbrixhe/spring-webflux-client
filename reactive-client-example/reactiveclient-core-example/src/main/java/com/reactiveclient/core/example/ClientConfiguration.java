package com.reactiveclient.core.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Bean
    public BeerClient helloClient(){
        return BeerClient.create();
    }
}
