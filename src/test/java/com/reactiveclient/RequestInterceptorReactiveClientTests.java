package com.reactiveclient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.Collections;
import java.util.stream.Stream;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RequestInterceptorReactiveClientTests.Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        value = {"spring.application.name=reactiveClientTest"})
@DirtiesContext
public class RequestInterceptorReactiveClientTests {

    @LocalServerPort
    private Integer port;

    @Test
    public void headerAddedByRequestInterceptor() {
        RequestInterceptorClient requestInterceptorClient = ClientBuilder
                .builder()
                .requestInterceptor(request -> request.addHeader("my-custom-header","My-awesome-custom-header"))
                .build(RequestInterceptorClient.class, URI.create("http://localhost:" + port));

        Mono<String> header = requestInterceptorClient.headerAddedByRequestInterceptor();
        StepVerifier.create(header)
                .expectNext("My-awesome-custom-header")
                .verifyComplete();
    }

    @Test
    public void multiHeadersAddedByMultipleRequestInterceptors() {
        RequestInterceptorClient requestInterceptorClient = ClientBuilder
                .builder()
                .requestInterceptor(request -> request.addHeader("my-custom-header-one","My-awesome-custom-header-one"))
                .requestInterceptor(request -> request.addHeader("my-custom-header-two","My-awesome-custom-header-two"))
                .build(RequestInterceptorClient.class, URI.create("http://localhost:" + port));

        Flux<String> headers = requestInterceptorClient.headersFromRequestInterceptors();
        StepVerifier.create(headers)
                .expectNext("My-awesome-custom-header-one", "My-awesome-custom-header-two")
                .verifyComplete();
    }

    @Test
    public void pathVariableModifiedByRequestInterceptor() {
        RequestInterceptorClient requestInterceptorClient = ClientBuilder
                .builder()
                .requestInterceptor(request -> request.getVariables().computeIfPresent("variable", (pathVariableName, pathVariableCurrentValue) -> String.class.cast(pathVariableCurrentValue) + "HasBeenModified"))
                .build(RequestInterceptorClient.class, URI.create("http://localhost:" + port));

        Mono<String> pathVariable = requestInterceptorClient.pathVariableModifiedByRequestInterceptor("VariableValue");
        StepVerifier.create(pathVariable)
                .expectNext("VariableValueHasBeenModified")
                .verifyComplete();
    }

    private interface RequestInterceptorClient {

        @RequestMapping(method = RequestMethod.GET, path = "/headers")
        Flux<String> headersFromRequestInterceptors();

        @RequestMapping(method = RequestMethod.GET, path = "/header")
        Mono<String> headerAddedByRequestInterceptor();

        @RequestMapping(method = RequestMethod.GET, path = "/pathVariable/{variable}")
        Mono<String> pathVariableModifiedByRequestInterceptor(@PathVariable("variable") String variable);
    }

    @RestController
    @SpringBootApplication
    protected static class Application {

        public static void main(String[] args) {
            new SpringApplicationBuilder(RequestInterceptorReactiveClientTests.Application.class)
                    .properties("spring.application.name=reactiveClientTests")
                    .run(args);
        }

        @RequestMapping(method = RequestMethod.GET, path = "/headers")
        public Flux<String> headersFromRequestInterceptors(@RequestHeader("my-custom-header-one")String customHeaderOne, @RequestHeader("my-custom-header-two")String customHeaderTwo){
            return Flux.fromStream(Stream.of(customHeaderOne, customHeaderTwo));
        }

        @RequestMapping(method = RequestMethod.GET, path = "/header")
        public Mono<String> headerFromRequestInterceptor(@RequestHeader("my-custom-header")String customHeader){
            return Mono.just(customHeader);
        }

        @RequestMapping(method = RequestMethod.GET, path = "/pathVariable/{variable}")
        public Mono<String> pathVariableModifiedByRequestInterceptor(@PathVariable("variable") String variable){
            return Mono.just(variable);
        }
    }
}
