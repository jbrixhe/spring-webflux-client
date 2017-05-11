package com.webfluxclient;


import com.webfluxclient.codec.ErrorDecoder;
import com.webfluxclient.codec.ExtendedClientCodecConfigurer;
import com.webfluxclient.codec.HttpErrorReader;
import com.webfluxclient.handler.ReactiveInvocationHandlerFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultClientBuilderTest {

    @Mock
    private ReactiveInvocationHandlerFactory reactiveInvocationHandlerFactory;

    @Captor
    private ArgumentCaptor<ExtendedClientCodecConfigurer> captor;

    @Test
    public void registerDefaultCodecs_withDefaultCodecsDisable(){
        URI targetUri = URI.create("http://example.ca");
        when(reactiveInvocationHandlerFactory.build(captor.capture(), any(RequestInterceptor.class), eq(TestClient.class), same(targetUri))).thenReturn(new MockInvocationHandler());

        create().registerDefaultCodecs(false)
                .build(TestClient.class, targetUri);

        ExtendedClientCodecConfigurer codecConfigurer = captor.getValue();
        assertThat(codecConfigurer.getReaders())
                .isEmpty();
        assertThat(codecConfigurer.getWriters())
                .isEmpty();
        assertThat(codecConfigurer.getErrorReaders())
                .isEmpty();

        verify(reactiveInvocationHandlerFactory).build(same(codecConfigurer), any(RequestInterceptor.class), eq(TestClient.class), same(targetUri));
        verifyNoMoreInteractions(reactiveInvocationHandlerFactory);
    }

    @Test
    public void registerDefaultCodecs_withDefaultCodecsEnable(){
        URI targetUri = URI.create("http://example.ca");
        when(reactiveInvocationHandlerFactory.build(captor.capture(), any(RequestInterceptor.class), eq(TestClient.class), same(targetUri))).thenReturn(new MockInvocationHandler());

        create().build(TestClient.class, targetUri);

        ExtendedClientCodecConfigurer codecConfigurer = captor.getValue();
        assertThat(codecConfigurer.getReaders())
                .isNotEmpty();
        assertThat(codecConfigurer.getWriters())
                .isNotEmpty();
        assertThat(codecConfigurer.getErrorReaders())
                .hasSize(2);

        verify(reactiveInvocationHandlerFactory).build(same(codecConfigurer), any(RequestInterceptor.class), eq(TestClient.class), same(targetUri));
        verifyNoMoreInteractions(reactiveInvocationHandlerFactory);
    }

    @Test
    public void registerDefaultCodecs_withCustomErrorDecoder(){
        URI targetUri = URI.create("http://example.ca");
        when(reactiveInvocationHandlerFactory.build(captor.capture(), any(RequestInterceptor.class), eq(TestClient.class), same(targetUri))).thenReturn(new MockInvocationHandler());

        create()
                .customCodecs(customCodecsConfigurer -> customCodecsConfigurer.errorDecoder(ErrorDecoder.of(HttpStatus.BAD_REQUEST::equals, (httpStatus, dataBuffer) -> new IllegalArgumentException())))
                .build(TestClient.class, targetUri);

        ExtendedClientCodecConfigurer codecConfigurer = captor.getValue();
        assertThat(codecConfigurer.getReaders())
                .isNotEmpty();
        assertThat(codecConfigurer.getWriters())
                .isNotEmpty();
        assertThat(codecConfigurer.getErrorReaders())
                .hasSize(3);

        assertThat(findReader(codecConfigurer.getErrorReaders(), HttpStatus.BAD_REQUEST))
                .isNotEmpty();
        verify(reactiveInvocationHandlerFactory).build(same(codecConfigurer), any(RequestInterceptor.class), eq(TestClient.class), same(targetUri));
        verifyNoMoreInteractions(reactiveInvocationHandlerFactory);
    }

    Optional<HttpErrorReader> findReader(List<HttpErrorReader> httpErrorReaders, HttpStatus httpStatus) {
        return httpErrorReaders.stream().filter(httpErrorReader -> httpErrorReader.canRead(httpStatus)).findFirst();
    }

    private DefaultClientBuilder create(){
        return new DefaultClientBuilder(reactiveInvocationHandlerFactory);
    }

    interface TestClient{}
}