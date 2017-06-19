package com.webfluxclient.codec;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

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
    public void getErrorReaders_withDefaultNotRegistered() {
        clientCodecConfigurer.registerDefaults(false);
        assertThat(clientCodecConfigurer.getErrorReaders())
                .isEmpty();
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
    public void getReaders_withDefaultNotRegistered() {
        clientCodecConfigurer.registerDefaults(false);
        assertThat(clientCodecConfigurer.getReaders())
                .isEmpty();
    }

    @Test
    public void getWriters_withDefaultNotRegistered() {
        clientCodecConfigurer.registerDefaults(false);
        assertThat(clientCodecConfigurer.getWriters())
                .isEmpty();
    }

    private static class CustomHttpClientErrorDecoder extends HttpClientErrorDecoder {

    }

    private static class CustomHttpServerErrorDecoder extends HttpServerErrorDecoder {

    }
}