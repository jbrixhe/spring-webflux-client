package com.reactiveclient.example.client;

import com.reactiveclient.ReactiveClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Bean
    public BeerClient helloClient(){
        return ReactiveClientBuilder
                .builder()
                .errorDecoder(new NotFoundErrorDecoder())
                .build(BeerClient.class, "http://localhost:8080");
    }
}
