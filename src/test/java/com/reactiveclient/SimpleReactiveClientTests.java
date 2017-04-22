package com.reactiveclient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.Serializable;
import java.net.URI;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SimpleReactiveClientTests.Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        value = {"spring.application.name=reactiveClientTest"})
@DirtiesContext
public class SimpleReactiveClientTests {

    @LocalServerPort
    private Integer port;

    @Test
    public void getHello() {
        Mono<Hello> hello = HelloClient.create("http://localhost:" + port).getHello();
        StepVerifier.create(hello)
                .expectNext(new Hello("Hello world 1"))
                .verifyComplete();
    }

    @Test
    public void getHellos() {
        Flux<Hello> hellos = HelloClient.create("http://localhost:" + port).getHellos();
        StepVerifier.create(hellos)
                .expectNext(new Hello("Hello world 1"),
                        new Hello("Hello world 2"),
                        new Hello("Hello world 3"),
                        new Hello("Hello world 4"))
                .verifyComplete();
    }

    @Test
    public void postHello() {
        Mono<Hello> hello = HelloClient.create("http://localhost:" + port).addHello(new Hello("Hello"));
        StepVerifier
                .create(hello)
                .expectNext(new Hello("Hello World!"))
                .verifyComplete();
    }

    @Test
    public void postHelloFromMono() {
        Mono<Hello> hello = HelloClient.create("http://localhost:" + port).addHelloFromMono(Mono.just(new Hello("Hello")));
        StepVerifier
                .create(hello)
                .expectNext(new Hello("Hello World!"))
                .verifyComplete();
    }

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Hello implements Serializable {
        private String message;
    }

    private interface HelloClient {
        static HelloClient create(String url) {
            return ClientBuilder
                    .builder()
                    .build(HelloClient.class, URI.create(url));
        }

        @RequestMapping(method = RequestMethod.GET, path = "/hellos/first")
        Mono<Hello> getHello();

        @RequestMapping(method = RequestMethod.GET, path = "/hellos")
        Flux<Hello> getHellos();

        @RequestMapping(method = RequestMethod.POST, path = "/hellos")
        Mono<Hello> addHello(Hello newHello);

        @RequestMapping(method = RequestMethod.POST, path = "/hellos")
        Mono<Hello> addHelloFromMono(Mono<Hello> newHello);
    }

    @RestController
    @SpringBootApplication
    protected static class Application {

        public static void main(String[] args) {
            new SpringApplicationBuilder(SimpleReactiveClientTests.Application.class)
                    .properties("spring.application.name=reactiveClientTests")
                    .run(args);
        }

        @RequestMapping(method = RequestMethod.POST, path = "/hellos")
        public Mono<Hello> addHello(@RequestBody Mono<Hello> newHello) {
            return Mono.from(newHello)
                    .map(hello -> new Hello(hello.getMessage() + " World!"));
        }

        @RequestMapping(method = RequestMethod.GET, path = "/hellos/first")
        public Mono<Hello> getHello() {
            return Mono.just(new Hello("Hello world 1"));
        }

        @RequestMapping(method = RequestMethod.GET, path = "/hellos")
        public Flux<Hello> getHellos() {
            return Flux.just(new Hello("Hello world 1"),
                    new Hello("Hello world 2"),
                    new Hello("Hello world 3"),
                    new Hello("Hello world 4"));
        }
    }
}
