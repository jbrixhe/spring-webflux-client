/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reactiveclient.test;

import com.reactiveclient.EnableReactiveClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ReactiveClientTests.Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        value = {"spring.application.name=reactiveClientTest", "server.port=8080"})
@DirtiesContext
public class ReactiveClientTests {

    @Autowired
    private HelloClient helloClient;

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Hello implements Serializable {
        private String message;
    }

    @RestController
    @EnableReactiveClient
    @SpringBootApplication
    protected static class Application {

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

        @RequestMapping(method = RequestMethod.GET, path = "/string")
        public Mono<String> getString(@RequestParam("name") List<String> names) {
            return Mono.just("MyString");
        }

        public static void main(String[] args) {
            new SpringApplicationBuilder(ReactiveClientTests.Application.class)
                    .properties("spring.application.name=reactiveClientTests")
                    .run(args);
        }
    }

    @Test
    public void testClient() {
        String block1 = helloClient.getString(Arrays.asList("jeremy", "julien"))
                .block();
        System.out.println(block1);
    }
}
