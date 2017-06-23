package com.webfluxclient.codec;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DefaultExtendedClientCodecConfigurerTest {

    @InjectMocks
    private DefaultExtendedClientCodecConfigurer clientCodecConfigurer;

    @Test
    public void getErrorReaders() {
        assertThat(clientCodecConfigurer.getErrorReaders())
                .hasOnlyElementsOfType(DecoderHttpErrorReader.class)
                .extracting("errorDecoder")
                .hasAtLeastOneElementOfType(HttpClientErrorDecoder.class)
                .hasAtLeastOneElementOfType(HttpServerErrorDecoder.class);
    }

    @Test
    public void getErrorReaders_withOverrideDefaultErrorDecoder() {
        clientCodecConfigurer.defaultCodecs().httpClientErrorDecoder(new CustomHttpClientErrorDecoder());
        clientCodecConfigurer.defaultCodecs().httpServerErrorDecoder(new CustomHttpServerErrorDecoder());

        assertThat(clientCodecConfigurer.getErrorReaders())
                .hasOnlyElementsOfType(DecoderHttpErrorReader.class)
                .extracting("errorDecoder")
                .hasAtLeastOneElementOfType(CustomHttpClientErrorDecoder.class)
                .hasAtLeastOneElementOfType(CustomHttpServerErrorDecoder.class);
    }

    @Test
    public void getErrorReaders_withCustomErrorDecoder() {
        clientCodecConfigurer.registerDefaults(false);
        clientCodecConfigurer.customCodecs().errorDecoder(new CustomErrorDecoder());
        assertThat(clientCodecConfigurer.getErrorReaders())
                .hasOnlyElementsOfType(DecoderHttpErrorReader.class)
                .extracting("errorDecoder")
                .hasAtLeastOneElementOfType(CustomErrorDecoder.class);
    }

    @Test
    public void getErrorReaders_withCustomHttpErrorReader() {
        clientCodecConfigurer.registerDefaults(false);
        clientCodecConfigurer.customCodecs().errorReader(new CustomHttpErrorReader());
        assertThat(clientCodecConfigurer.getErrorReaders())
                .hasOnlyElementsOfType(CustomHttpErrorReader.class);
    }

    @Test
    public void getErrorReaders_withVerifiedOrder() {
        clientCodecConfigurer.customCodecs().errorReader(new CustomHttpErrorReader());
        List<HttpErrorReader> errorReaders = clientCodecConfigurer.getErrorReaders();
        assertThat(errorReaders)
                .hasSize(3);
        assertThat(errorReaders.get(0))
                .isInstanceOf(CustomHttpErrorReader.class);
        assertThat(errorReaders.get(1))
                .isInstanceOf(DecoderHttpErrorReader.class)
                .extracting("errorDecoder")
                .allMatch(HttpClientErrorDecoder.class::isInstance);
        assertThat(errorReaders.get(2))
                .isInstanceOf(DecoderHttpErrorReader.class)
                .extracting("errorDecoder")
                .allMatch(HttpServerErrorDecoder.class::isInstance);
    }

    @Test
    public void getReaders() {
        assertThat(clientCodecConfigurer.getReaders())
            .hasSize(8);
    }

    @Test
    public void getWriters() {
        assertThat(clientCodecConfigurer.getReaders())
                .hasSize(8);
    }

    @Test
    public void disableDefaultCodecs() {
        clientCodecConfigurer.registerDefaults(false);
        assertThat(clientCodecConfigurer.getReaders())
                .isEmpty();
        assertThat(clientCodecConfigurer.getErrorReaders())
                .isEmpty();
        assertThat(clientCodecConfigurer.getWriters())
                .isEmpty();
    }

    @Test
    public void override_serverSentEventDecoder() {
        clientCodecConfigurer.defaultCodecs().serverSentEventDecoder(new CustomServerSentEventDecoder());

        assertThat(clientCodecConfigurer.getReaders())
            .extracting("decoder")
            .hasAtLeastOneElementOfType(CustomServerSentEventDecoder.class);
    }

    @Test
    public void addCustomHttpMessageReader() {
        clientCodecConfigurer.registerDefaults(false);
        clientCodecConfigurer.customCodecs().reader(new CustomHttpMessageReader());

        assertThat(clientCodecConfigurer.getReaders())
                .hasSize(1)
                .hasAtLeastOneElementOfType(CustomHttpMessageReader.class);
    }

    @Test
    public void addCustomDecoder() {
        clientCodecConfigurer.registerDefaults(false);
        clientCodecConfigurer.customCodecs().decoder(new CustomDecoder());

        assertThat(clientCodecConfigurer.getReaders())
                .hasSize(1)
                .extracting("decoder")
                .hasAtLeastOneElementOfType(CustomDecoder.class);
    }

    @Test
    public void addCustomHttpMessageWriter() {
        clientCodecConfigurer.registerDefaults(false);
        clientCodecConfigurer.customCodecs().writer(new CustomHttpMessageWriter());

        assertThat(clientCodecConfigurer.getWriters())
                .hasSize(1)
                .hasAtLeastOneElementOfType(CustomHttpMessageWriter.class);
    }

    @Test
    public void addCustomEncoder() {
        clientCodecConfigurer.registerDefaults(false);
        clientCodecConfigurer.customCodecs().encoder(new CustomEncoder());

        assertThat(clientCodecConfigurer.getWriters())
                .hasSize(1)
                .extracting("encoder")
                .hasAtLeastOneElementOfType(CustomEncoder.class);
    }

    private static class CustomServerSentEventDecoder implements Decoder {

        @Override
        public boolean canDecode(ResolvableType resolvableType, @Nullable MimeType mimeType) {
            return false;
        }

        @Override
        public List<MimeType> getDecodableMimeTypes() {
            return null;
        }

        @Override
        public Mono decodeToMono(Publisher publisher, ResolvableType resolvableType, @Nullable MimeType mimeType, @Nullable Map map) {
            return null;
        }

        @Override
        public Flux decode(Publisher publisher, ResolvableType resolvableType, @Nullable MimeType mimeType, @Nullable Map map) {
            return null;
        }
    }

    private static class CustomHttpMessageReader implements HttpMessageReader {

        @Override
        public List<MediaType> getReadableMediaTypes() {
            return null;
        }

        @Override
        public boolean canRead(ResolvableType resolvableType, @Nullable MediaType mediaType) {
            return false;
        }

        @Override
        public Mono readMono(ResolvableType resolvableType, ReactiveHttpInputMessage reactiveHttpInputMessage, Map map) {
            return null;
        }

        @Override
        public Flux read(ResolvableType resolvableType, ReactiveHttpInputMessage reactiveHttpInputMessage, Map map) {
            return null;
        }
    }

    private static class CustomHttpMessageWriter implements HttpMessageWriter {

        @Override
        public List<MediaType> getWritableMediaTypes() {
            return null;
        }

        @Override
        public boolean canWrite(ResolvableType resolvableType, @Nullable MediaType mediaType) {
            return false;
        }

        @Override
        public Mono<Void> write(Publisher publisher, ResolvableType resolvableType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage reactiveHttpOutputMessage, Map map) {
            return null;
        }
    }

    private static class CustomDecoder implements Decoder {

        @Override
        public boolean canDecode(ResolvableType resolvableType, @Nullable MimeType mimeType) {
            return false;
        }

        @Override
        public List<MimeType> getDecodableMimeTypes() {
            return Collections.emptyList();
        }

        @Override
        public Mono decodeToMono(Publisher publisher, ResolvableType resolvableType, @Nullable MimeType mimeType, @Nullable Map map) {
            return null;
        }

        @Override
        public Flux decode(Publisher publisher, ResolvableType resolvableType, @Nullable MimeType mimeType, @Nullable Map map) {
            return null;
        }
    }

    private static class CustomEncoder implements Encoder {

        @Override
        public boolean canEncode(ResolvableType resolvableType, @Nullable MimeType mimeType) {
            return false;
        }

        @Override
        public List<MimeType> getEncodableMimeTypes() {
            return Collections.emptyList();
        }

        @Override
        public Flux<DataBuffer> encode(Publisher publisher, DataBufferFactory dataBufferFactory, ResolvableType resolvableType, @Nullable MimeType mimeType, @Nullable Map map) {
            return null;
        }
    }

    private static class CustomHttpErrorReader implements HttpErrorReader {

        @Override
        public boolean canRead(HttpStatus httpStatus) {
            return false;
        }

        @Override
        public <T> Flux<T> read(ClientHttpResponse inputMessage) {
            return null;
        }

        @Override
        public <T> Mono<T> readMono(ClientHttpResponse inputMessage) {
            return null;
        }
    }

    private static class CustomErrorDecoder implements ErrorDecoder {

        @Override
        public boolean canDecode(HttpStatus httpStatus) {
            return true;
        }

        @Override
        public RuntimeException decode(HttpStatus httpStatus, DataBuffer inputStream) {
            return null;
        }
    }

    private static class CustomHttpClientErrorDecoder extends HttpClientErrorDecoder {

    }

    private static class CustomHttpServerErrorDecoder extends HttpServerErrorDecoder {

    }
}