package com.reactiveclient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

import static java.util.Arrays.asList;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BodyInserterReactiveClientTests.Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        value = {"spring.application.name=reactiveClientTest"})
@DirtiesContext
public class BodyInserterReactiveClientTests {

    private static String[] requestParamMultipleValues = new String[]{"ParamOne", "ParamTwo", "ParamThree"};

    @LocalServerPort
    private Integer port;

    @Value("classpath:/resource-to-send-as-request-body.txt")
    private Resource requestBody;

    @Test
    public void bodyAsMono(){
        Mono<String> response = BodyInserterClient.create("http://localhost:" + port).bodyAsMono(Mono.just("Hello world"));
        StepVerifier.create(response)
            .expectNext("Hello world")
            .verifyComplete();
    }

    @Test
    public void bodyAsDataBufferInMono(){
        DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap("Hello world".getBytes());
        Mono<String> response = BodyInserterClient.create("http://localhost:" + port).bodyAsDataBufferInMono(Mono.just(dataBuffer));
        StepVerifier.create(response)
                .expectNext("Hello world")
                .verifyComplete();
    }

    @Test
    public void bodyAsFlux(){
        Flux<String> body = Flux.fromIterable(asList("Hello", "world"));
        Flux<String> response = BodyInserterClient.create("http://localhost:" + port).bodyAsFlux(body);
        StepVerifier.create(response)
                .expectNext("Hello")
                .expectNext("world")
                .verifyComplete();
    }

    @Test
    public void bodyAsDataBufferInFlux(){
        DefaultDataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
        Flux<DataBuffer> body = Flux.fromIterable(asList(dataBufferFactory.wrap("Hello".getBytes()), dataBufferFactory.wrap("world".getBytes())));
        Flux<String> response = BodyInserterClient.create("http://localhost:" + port).bodyAsDataBufferInFlux(body);
        StepVerifier.create(response)
                .expectNext("Hello")
                .expectNext("world")
                .verifyComplete();
    }

    @Test
    public void bodyAsResource(){
        Mono<String> response = BodyInserterClient.create("http://localhost:" + port).bodyAsResource(requestBody);
        StepVerifier.create(response)
                .expectNext("Hello world")
                .verifyComplete();
    }

    private interface BodyInserterClient {
        static BodyInserterClient create(String url) {
            return ClientBuilder
                    .builder()
                    .build(BodyInserterClient.class, URI.create(url));
        }

        @PostMapping(path = "/bodys/mono")
        Mono<String> bodyAsMono(Mono<String> body);

        @PostMapping(path = "/bodys/mono")
        Mono<String> bodyAsDataBufferInMono(Mono<DataBuffer> body);

        @PostMapping(path = "/bodys/flux")
        Flux<String> bodyAsFlux(Flux<String> body);

        @PostMapping(path = "/bodys/flux")
        Flux<String> bodyAsDataBufferInFlux(Flux<DataBuffer> body);

        @PostMapping(path = "/bodys/resources")
        Mono<String> bodyAsResource(Resource body);
    }


    @RestController
    @SpringBootApplication
    protected static class Application {

        public static void main(String[] args) {
            new SpringApplicationBuilder(Application.class)
                    .properties("spring.application.name=reactiveClientTests")
                    .run(args);
        }

        @PostMapping(path = "/bodys/mono")
        Mono<String> bodyAsMono(@RequestBody Mono<String> body){
            return Mono.from(body);
        }

        @PostMapping(path = "/bodys/flux")
        Flux<String> bodyAsFlux(@RequestBody Flux<String> body){
            return Flux.from(body);
        }

        @PostMapping(path = "/bodys/resources")
        Mono<String> bodyAsResource(@RequestBody String body){
            return Mono.just(body);
        }
    }
}
