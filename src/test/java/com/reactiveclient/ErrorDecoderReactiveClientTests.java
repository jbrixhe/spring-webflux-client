package com.reactiveclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.client.HttpServerErrorException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.reactiveclient.client.DataBuffers.readToString;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ErrorDecoderReactiveClientTests.Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        value = {"spring.application.name=reactiveClientTest"})
@DirtiesContext
public class ErrorDecoderReactiveClientTests {
    private static final String NOT_FOUND_EXCEPTION_MESSAGE = "The resource you requested doesn't exist";
    private static final String INTERNAL_SERVER_EXCEPTION_MESSAGE = "Internal server exception";

    @LocalServerPort
    private Integer port;

    @Test
    public void mono_withDefaultErrorDecoder() {
        Mono<SimpleDTO> hello = ErrorDecoderClient.create("http://localhost:" + port).monoWithInternalException();
        StepVerifier.create(hello)
                .consumeErrorWith(this::assertHttpServerErrorException)
                .verify();
    }

    @Test
    public void mono_withSimpleExceptionMessage() {
        Mono<SimpleDTO> hello = ErrorDecoderClient.create("http://localhost:" + port).monoWithNotFoundException();
        StepVerifier.create(hello)
                .consumeErrorWith(this::assertNotFoundException)
                .verify();
    }

    @Test
    public void mono_withCustomExceptionPayload() {
        Mono<SimpleDTO> hello = ErrorDecoderClient.create("http://localhost:" + port).monoWithBadRequestException();
        StepVerifier.create(hello)
                .consumeErrorWith(this::assertBadRequestException)
                .verify();
    }

    @Test
    public void flux_withSimpleExceptionMessage() {
        Flux<SimpleDTO> hellos = ErrorDecoderClient.create("http://localhost:" + port).fluxWithNotFoundException();
        StepVerifier.create(hellos)
                .consumeErrorWith(this::assertNotFoundException)
                .verify();
    }

    @Test
    public void flux_withDefaultErrorDecoder() {
        Flux<SimpleDTO> hellos = ErrorDecoderClient.create("http://localhost:" + port).fluxWithInternalException();
        StepVerifier.create(hellos)
                .consumeErrorWith(this::assertHttpServerErrorException)
                .verify();
    }

    @Test
    public void flux_withCustomExceptionPayload() {
        Flux<SimpleDTO> hellos = ErrorDecoderClient.create("http://localhost:" + port).fluxWithBadRequestException();
        StepVerifier.create(hellos)
                .consumeErrorWith(this::assertBadRequestException)
                .verify();
    }

    private interface ErrorDecoderClient {
        static ErrorDecoderClient create(String url) {
            return ClientBuilder
                    .builder()
                    .errorDecoder(new BadRequestErrorDecoder())
                    .errorDecoder(new NotFoundErrorDecoder())
                    .build(ErrorDecoderClient.class, URI.create(url));
        }

        @GetMapping(path = "/internal/mono")
        Mono<SimpleDTO> monoWithInternalException();

        @GetMapping(path = "/internal/flux")
        Flux<SimpleDTO> fluxWithInternalException();

        @GetMapping(path = "/notFound/mono")
        Mono<SimpleDTO> monoWithNotFoundException();

        @GetMapping(path = "/notFound/flux")
        Flux<SimpleDTO> fluxWithNotFoundException();

        @GetMapping(path = "/badRequest/mono")
        Mono<SimpleDTO> monoWithBadRequestException();

        @GetMapping(path = "/badRequest/flux")
        Flux<SimpleDTO> fluxWithBadRequestException();
    }

    private void assertHttpServerErrorException(Throwable throwable) {
        assertThat(throwable)
                .isInstanceOf(HttpServerErrorException.class)
                .extracting("statusCode", "message")
                .containsExactly(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value()+ " " +INTERNAL_SERVER_EXCEPTION_MESSAGE);
    }

    private void assertNotFoundException(Throwable throwable) {
        assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .extracting("message")
                .containsExactly(NOT_FOUND_EXCEPTION_MESSAGE);
    }

    private void assertBadRequestException(Throwable throwable) {
        assertThat(throwable)
                .isInstanceOf(BadRequestException.class)
                .extracting("validationErrors")
                .containsExactly(singletonList(new ValidationError("message", "null", "may not be null")));
    }

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    private static class SimpleDTO implements Serializable {
        @NotNull
        private String message;
    }

    @RestController
    @SpringBootApplication
    protected static class Application {

        public static void main(String[] args) {
            new SpringApplicationBuilder(ErrorDecoderReactiveClientTests.Application.class)
                    .properties("spring.application.name=reactiveClientTests")
                    .run(args);
        }

        @GetMapping(path = "/internal/mono")
        public Mono<SimpleDTO> monoWithInternalException(){
            throw new RuntimeException(INTERNAL_SERVER_EXCEPTION_MESSAGE);
        }

        @GetMapping(path = "/internal/flux")
        public Flux<SimpleDTO> fluxWithInternalException() {
            throw new RuntimeException(INTERNAL_SERVER_EXCEPTION_MESSAGE);
        }

        @GetMapping(path = "/notFound/mono")
        public Mono<SimpleDTO> monoWithNotFoundException() {
            throw new NotFoundException(NOT_FOUND_EXCEPTION_MESSAGE);
        }

        @GetMapping(path = "/notFound/flux")
        public Flux<SimpleDTO> fluxWithNotFoundException() {
            throw new NotFoundException(NOT_FOUND_EXCEPTION_MESSAGE);
        }

        @GetMapping(path = "/badRequest/mono")
        public Mono<SimpleDTO> monoWithBadRequestException() {
            throw new BadRequestException(singletonList(new ValidationError("message", "null", "may not be null")));
        }

        @GetMapping(path = "/badRequest/flux")
        public Flux<SimpleDTO> fluxWithBadRequestException() {
            throw new BadRequestException(singletonList(new ValidationError("message", "null", "may not be null")));
        }

        @ExceptionHandler({NotFoundException.class})
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public String handleNotFoundException(NotFoundException e) {
            return e.getMessage();
        }

        @ExceptionHandler({BadRequestException.class})
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ResponseBody
        public List<ValidationError> handleBadRequestException(BadRequestException e) {
            return e.getValidationErrors();
        }

        @ExceptionHandler({Exception.class})
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public String handleException(Exception e) {
            return e.getMessage();
        }
    }

    static class BadRequestErrorDecoder implements ErrorDecoder<BadRequestException> {

        private final ObjectReader objectReader;

        public BadRequestErrorDecoder() {
            objectReader = new ObjectMapper().readerFor(new TypeReference<List<ValidationError>>() {});
        }

        @Override
        public boolean canDecode(HttpStatus httpStatus) {
            return HttpStatus.BAD_REQUEST.equals(httpStatus);
        }

        @Override
        public BadRequestException decode(HttpStatus httpStatus, DataBuffer inputMessage) {
            try {
                return new BadRequestException(objectReader.readValue(inputMessage.asInputStream()));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    static class NotFoundErrorDecoder implements ErrorDecoder<NotFoundException> {

        @Override
        public boolean canDecode(HttpStatus httpStatus) {
            return HttpStatus.NOT_FOUND.equals(httpStatus);
        }

        @Override
        public NotFoundException decode(HttpStatus httpStatus, DataBuffer inputMessage) {
            return new NotFoundException(readToString(inputMessage));
        }
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }

    @Getter
    private static class BadRequestException extends IllegalArgumentException {
        private List<ValidationError> validationErrors;

        BadRequestException(List<ValidationError> validationErrors) {
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
