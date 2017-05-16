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
public class HttpServerErrorDecoderTest {
    
    @InjectMocks
    private HttpServerErrorDecoder httpServerErrorDecoder;
    
    @Test
    public void canDecode() {
        Stream.of(HttpStatus.values())
                .forEach(httpStatus -> assertThat(httpServerErrorDecoder.canDecode(httpStatus))
                        .isEqualTo(httpStatus.is5xxServerError()));
    }
    
    @Test
    public void decode() {
        String exceptionMessage = "Hello Canada";
        assertThat(httpServerErrorDecoder.decode(HttpStatus.INTERNAL_SERVER_ERROR, create(exceptionMessage)))
                .isInstanceOf(HttpServerException.class)
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
                .hasMessage(exceptionMessage);
    }
    
    private DataBuffer create(String value) {
        return new DefaultDataBufferFactory().wrap(value.getBytes());
    }
}