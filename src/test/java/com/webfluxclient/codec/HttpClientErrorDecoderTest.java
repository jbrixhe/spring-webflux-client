package com.webfluxclient.codec;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class HttpClientErrorDecoderTest {
    
    @InjectMocks
    private HttpClientErrorDecoder httpClientErrorDecoder;
    
    @Test
    public void canDecode() {
        Stream.of(HttpStatus.values())
                .forEach(httpStatus -> assertThat(httpClientErrorDecoder.canDecode(httpStatus))
                        .isEqualTo(httpStatus.is4xxClientError()));
    }
    
    @Test
    public void decode() {
        String exceptionMessage = "Hello Canada";
        assertThat(httpClientErrorDecoder.decode(HttpStatus.BAD_REQUEST, create(exceptionMessage)))
                .isInstanceOf(HttpClientException.class)
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                .hasMessage(exceptionMessage);
    }
    
    private DataBuffer create(String value) {
        return new DefaultDataBufferFactory().wrap(value.getBytes());
    }
}