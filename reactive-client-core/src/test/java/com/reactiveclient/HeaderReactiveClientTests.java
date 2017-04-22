package com.reactiveclient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HeaderReactiveClientTests.Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        value = {"spring.application.name=reactiveClientTest"})
@DirtiesContext
public class HeaderReactiveClientTests {

    @LocalServerPort
    private Integer port;

    @Test
    public void headerFromMethodParam() {
        HeaderClient requestInterceptorClient = ClientBuilder
                .builder()
                .build(HeaderClient.class, URI.create("http://localhost:" + port));

        Mono<String> pathVariable = requestInterceptorClient.headerFromMethodParam("MyHeader");
        StepVerifier.create(pathVariable)
                .expectNext("MyHeader modified by the server")
                .verifyComplete();
    }

    private interface HeaderClient {

        @RequestMapping(method = RequestMethod.GET, path = "/header")
        Mono<String> headerFromMethodParam(@RequestHeader("my-custom-header") String header);
    }

    @RestController
    @SpringBootApplication
    protected static class Application {

        public static void main(String[] args) {
            new SpringApplicationBuilder(HeaderReactiveClientTests.Application.class)
                    .properties("spring.application.name=reactiveClientTests")
                    .run(args);
        }

        @RequestMapping(method = RequestMethod.GET, path = "/header")
        public Mono<String> headerFromRequestInterceptor(@RequestHeader("my-custom-header") String customHeader){
            return Mono.just(customHeader + " modified by the server");
        }
    }
}
