package com.reactiveclient.example.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.Serializable;

@RestController
public class HelloController {

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Hello implements Serializable {
        private String message;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/hello")
    public Mono<Hello> getHello() {
        return Mono.just(new Hello("hello world 1"));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/hellos")
    public Flux<Hello> getHellos() {
        return Flux.just(new Hello("hello world 1"),
                new Hello("hello world 2"),
                new Hello("hello world 3"),
                new Hello("hello world 4"));
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public Mono<String> handleException(Exception e) {
        return Mono.error(e);
    }

}
