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

package com.reactiveclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactiveclient.client.StringErrorDecoder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CustomErrorDecoderReactiveClientTests.Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        value = {"spring.application.name=reactiveClientTest"})
@DirtiesContext
public class CustomErrorDecoderReactiveClientTests {
    private static final String NOT_FOUND_EXCEPTION_MESSAGE = "The resource you requested doesn't exist";

    @LocalServerPort
    private Integer port;

    @Test
    public void getHello_withNotFoundException() {
        Mono<Hello> hello = HelloClient.create("http://localhost:" + port).getHello();
        StepVerifier.create(hello)
                .expectErrorMatches(throwable -> NotFoundException.class.isInstance(throwable) && NOT_FOUND_EXCEPTION_MESSAGE.equals(throwable.getMessage()))
                .verify();
    }

    @Test
    public void getHello_withValidationException() {
        Mono<Hello> hello = HelloClient.create("http://localhost:" + port).addHello(Mono.fromSupplier(Hello::new));
        StepVerifier.create(hello)
                .consumeErrorWith(throwable -> Assertions.assertThat(throwable)
                        .isInstanceOf(ValidationException.class)
                        .extracting("validationErrors")
                        .containsExactly(Collections.singletonList(new ValidationError("message", "null", "may not be null"))))
                .verify();
    }

    private interface HelloClient {
        static HelloClient create(String url) {
            return ReactiveClientBuilder
                    .builder()
                    .errorDecoder(ErrorDecoders.notFoundExceptionDecoder())
                    .errorDecoder(ErrorDecoders.badRequestExceptionDecoder())
                    .build(HelloClient.class, url);
        }

        @RequestMapping(method = RequestMethod.GET, path = "/hellos/first")
        Mono<Hello> getHello();

        @RequestMapping(method = RequestMethod.POST, path = "/hellos")
        Mono<Hello> addHello(Mono<Hello> newHello);
    }

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Hello implements Serializable {
        @NotNull
        private String message;
    }

    @RestController
    @SpringBootApplication
    protected static class Application {

        public static void main(String[] args) {
            new SpringApplicationBuilder(CustomErrorDecoderReactiveClientTests.Application.class)
                    .properties("spring.application.name=reactiveClientTests")
                    .run(args);
        }

        @RequestMapping(method = RequestMethod.GET, path = "/hellos/first")
        public Mono<Hello> getHello() {
            return Mono.error(new NotFoundException(NOT_FOUND_EXCEPTION_MESSAGE));
        }

        @RequestMapping(method = RequestMethod.POST, path = "/hellos")
        public Mono<Hello> addHello(@Valid @RequestBody Mono<Hello> newHello) {
            // should never arrive here.
            return newHello;
        }

        @ExceptionHandler(NotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public String handleNotFoundException(NotFoundException e) {
            return e.getMessage();
        }

        @ExceptionHandler(WebExchangeBindException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public List<ValidationError> handleException(WebExchangeBindException e) {
            return e.getFieldErrors()
                    .stream()
                    .map(objectError -> new ValidationError(objectError.getField(), String.valueOf(objectError.getRejectedValue()), objectError.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }

    private static class ErrorDecoders {
        static ErrorDecoder notFoundExceptionDecoder() {
            return new StringErrorDecoder(HttpStatus.NOT_FOUND::equals, (httpStatus, responseBody) -> new NotFoundException(responseBody));
        }

        static ErrorDecoder badRequestExceptionDecoder() {
            ObjectMapper objectMapper = new ObjectMapper();
            return new StringErrorDecoder(HttpStatus.BAD_REQUEST::equals, (httpStatus, responseBody) -> {
                try {
                    return new ValidationException(objectMapper.readValue(responseBody, new TypeReference<List<ValidationError>>() {
                    }));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
    }

    private static class NotFoundException extends RuntimeException {
        NotFoundException(String message) {
            super(message);
        }
    }

    @Getter
    private static class ValidationException extends IllegalArgumentException {
        private List<ValidationError> validationErrors;

        ValidationException(List<ValidationError> validationErrors) {
            super();
            this.validationErrors = validationErrors;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    private static class ValidationError {
        private String field;
        private String rejectedValue;
        private String defaultMessage;
    }
}
