package com.reactiveclient.example.client;

import com.reactiveclient.ReactiveClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Bean
    public HelloClient helloClient(){
        return ReactiveClientBuilder.builder()
                .build(HelloClient.class, "http://localhost:8888");
    }
}
