package com.reactiveclient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CollectionReactiveClientTests.Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        value = {"spring.application.name=reactiveClientTest"})
@DirtiesContext
public class CollectionReactiveClientTests {

    private static String[] requestParamMultipleValues = new String[]{"ParamOne", "ParamTwo", "ParamThree"};

    @LocalServerPort
    private Integer port;

    @Test
    public void collectionAsRequestParam() {

        ClassUtils.isPresent("reactor.test.DefaultStepVerifierBuilder",
                CollectionReactiveClientTests.class.getClassLoader());
        Flux<String> hello = RequestParamMultipleValueClient.create("http://localhost:" + port).collectionAsRequestParam(Arrays.asList(requestParamMultipleValues));
        StepVerifier.create(hello)
                .expectNext(requestParamMultipleValues)
                .verifyComplete();
    }

    @Test
    public void arrayAsRequestParam() {
        Flux<String> hello = RequestParamMultipleValueClient.create("http://localhost:" + port).arrayAsRequestParam(requestParamMultipleValues);
        StepVerifier.create(hello)
                .expectNext(requestParamMultipleValues)
                .verifyComplete();
    }

    private interface RequestParamMultipleValueClient {
        static RequestParamMultipleValueClient create(String url) {
            return ClientBuilder
                    .builder()
                    .build(RequestParamMultipleValueClient.class, URI.create(url));
        }

        @RequestMapping(method = RequestMethod.GET, path = "/requestParams")
        Flux<String> arrayAsRequestParam(@RequestParam("multipleValuesRequestParam") String[] requestParamValues);

        @RequestMapping(method = RequestMethod.GET, path = "/requestParams")
        Flux<String> collectionAsRequestParam(@RequestParam("multipleValuesRequestParam") Collection<String> requestParamValues);
    }


    @RestController
    @SpringBootApplication
    protected static class Application {

        public static void main(String[] args) {
            new SpringApplicationBuilder(Application.class)
                    .properties("spring.application.name=reactiveClientTests")
                    .run(args);
        }

        @RequestMapping(method = RequestMethod.GET, path = "/requestParams")
        public Flux<String> collectionAsRequestParam(@RequestParam("multipleValuesRequestParam") List<String> requestParams) {
            return Flux.fromIterable(requestParams);
        }
    }
}
