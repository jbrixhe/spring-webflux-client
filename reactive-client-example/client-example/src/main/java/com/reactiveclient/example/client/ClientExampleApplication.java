package com.reactiveclient.example.client;

import com.reactiveclient.EnableReactiveClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;

@EnableReactiveClient
@SpringBootApplication
public class ClientExampleApplication {

    @Autowired
    private HelloClient helloClient;

    public static void main(String[] args) {
        SpringApplication.run(ClientExampleApplication.class, args);
    }

    @PostConstruct
    public void init() throws URISyntaxException {
        Hello block = helloClient.getHello()
                .block();
        System.out.println(block);
    }
}
