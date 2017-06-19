package com.webfluxclient;


import com.webfluxclient.codec.ErrorDecoder;
import com.webfluxclient.codec.ExtendedClientCodecConfigurer;
import com.webfluxclient.codec.HttpClientErrorDecoder;
import com.webfluxclient.codec.HttpErrorReader;
import com.webfluxclient.codec.HttpServerErrorDecoder;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultClientBuilderTest {

    @Mock
    private ReactiveInvocationHandlerFactory reactiveInvocationHandlerFactory;

    @Captor
    private ArgumentCaptor<ExtendedClientCodecConfigurer> codecConfigurerArgumentCaptor;
    
    @Captor
    private ArgumentCaptor<List<RequestInterceptor>> requestInterceptorsArgumentCaptor;

    @Test
    public void registerDefaultCodecs_withDefaultCodecsDisable(){
        URI targetUri = URI.create("http://example.ca");
        
        when(reactiveInvocationHandlerFactory.build(codecConfigurerArgumentCaptor.capture(), anyList(), eq(TestClient.class), same(targetUri))).thenReturn(new MockInvocationHandler());

        createBuilder().registerDefaultCodecs(false)
                .build(TestClient.class, targetUri);

        ExtendedClientCodecConfigurer codecConfigurer = codecConfigurerArgumentCaptor.getValue();
        assertThat(codecConfigurer.getReaders())
                .isEmpty();
        assertThat(codecConfigurer.getWriters())
                .isEmpty();
        assertThat(codecConfigurer.getErrorReaders())
                .isEmpty();

        verify(reactiveInvocationHandlerFactory).build(same(codecConfigurer), anyList(), eq(TestClient.class), same(targetUri));
        verifyNoMoreInteractions(reactiveInvocationHandlerFactory);
    }

    @Test
    public void registerDefaultCodecs_withDefaultCodecsEnable(){
        URI targetUri = URI.create("http://example.ca");
        
        when(reactiveInvocationHandlerFactory.build(codecConfigurerArgumentCaptor.capture(), anyList(), eq(TestClient.class), same(targetUri))).thenReturn(new MockInvocationHandler());

        createBuilder().build(TestClient.class, targetUri);

        ExtendedClientCodecConfigurer codecConfigurer = codecConfigurerArgumentCaptor.getValue();
        assertThat(codecConfigurer.getReaders())
                .isNotEmpty();
        assertThat(codecConfigurer.getWriters())
                .isNotEmpty();
        assertThat(codecConfigurer.getErrorReaders())
                .hasSize(2);

        verify(reactiveInvocationHandlerFactory).build(same(codecConfigurer), anyList(), eq(TestClient.class), same(targetUri));
        verifyNoMoreInteractions(reactiveInvocationHandlerFactory);
    }

    @Test
    public void registerDefaultCodecs_withCustomErrorDecoder(){
        URI targetUri = URI.create("http://example.ca");
        
        when(reactiveInvocationHandlerFactory.build(codecConfigurerArgumentCaptor.capture(), anyList(), eq(TestClient.class), same(targetUri))).thenReturn(new MockInvocationHandler());

        createBuilder()
                .registerDefaultCodecs(false)
                .customCodecs(customCodecsConfigurer -> customCodecsConfigurer.errorDecoder(ErrorDecoder.of(HttpStatus.BAD_REQUEST::equals, (httpStatus, dataBuffer) -> new IllegalArgumentException())))
                .build(TestClient.class, targetUri);

        ExtendedClientCodecConfigurer codecConfigurer = codecConfigurerArgumentCaptor.getValue();
        assertThat(codecConfigurer.getErrorReaders())
                .hasSize(1);
        assertThat(findReader(codecConfigurer.getErrorReaders(), HttpStatus.BAD_REQUEST))
                .isNotEmpty();
        verify(reactiveInvocationHandlerFactory).build(same(codecConfigurer), anyList(), eq(TestClient.class), same(targetUri));
        verifyNoMoreInteractions(reactiveInvocationHandlerFactory);
    }
    
    @Test
    public void registerDefaultCodecs_withDefaultErrorDecoderOverride(){
        URI targetUri = URI.create("http://example.ca");
        OverrideHttpClientErrorDecoder clientErrorDecoder = new OverrideHttpClientErrorDecoder();
        OverrideHttpServerErrorDecoder serverErrorDecoder = new OverrideHttpServerErrorDecoder();
    
        when(reactiveInvocationHandlerFactory.build(codecConfigurerArgumentCaptor.capture(), anyList(), eq(TestClient.class), same(targetUri))).thenReturn(new MockInvocationHandler());
        
        createBuilder()
                .defaultCodecs(defaultCodecsConfigurerConsumer -> {
                    defaultCodecsConfigurerConsumer.httpClientErrorDecoder(clientErrorDecoder);
                    defaultCodecsConfigurerConsumer.httpServerErrorDecoder(serverErrorDecoder);
                })
                .build(TestClient.class, targetUri);
        
        ExtendedClientCodecConfigurer codecConfigurer = codecConfigurerArgumentCaptor.getValue();
        assertThat(codecConfigurer.getErrorReaders())
                .hasSize(2)
                .extracting("errorDecoder")
                .containsExactlyInAnyOrder(clientErrorDecoder, serverErrorDecoder);
        
        verify(reactiveInvocationHandlerFactory).build(same(codecConfigurer), anyList(), eq(TestClient.class), same(targetUri));
        verifyNoMoreInteractions(reactiveInvocationHandlerFactory);
    }
    
    @Test
    public void registerDefaultCodecs_withErrorInterceptor(){
        URI targetUri = URI.create("http://example.ca");
        RequestInterceptor requestInterceptor = request -> {System.out.println(request);};
        when(reactiveInvocationHandlerFactory.build(any(ExtendedClientCodecConfigurer.class), requestInterceptorsArgumentCaptor.capture(), eq(TestClient.class), same(targetUri))).thenReturn(new MockInvocationHandler());
    
        createBuilder()
                .requestInterceptor(requestInterceptor)
                .build(TestClient.class, targetUri);
        
        List<RequestInterceptor> requestInterceptors = requestInterceptorsArgumentCaptor.getValue();
        assertThat(requestInterceptors)
                .hasSize(1)
                .containsExactlyInAnyOrder(requestInterceptor);
        
        verify(reactiveInvocationHandlerFactory).build(any(ExtendedClientCodecConfigurer.class), same(requestInterceptors), eq(TestClient.class), same(targetUri));
        verifyNoMoreInteractions(reactiveInvocationHandlerFactory);
    }

    Optional<HttpErrorReader> findReader(List<HttpErrorReader> httpErrorReaders, HttpStatus httpStatus) {
        return httpErrorReaders.stream().filter(httpErrorReader -> httpErrorReader.canRead(httpStatus)).findFirst();
    }

    private DefaultClientBuilder createBuilder(){
        return new DefaultClientBuilder(reactiveInvocationHandlerFactory);
    }

    interface TestClient{}
    
    class OverrideHttpClientErrorDecoder extends HttpClientErrorDecoder {
    
    }
    
    class OverrideHttpServerErrorDecoder extends HttpServerErrorDecoder {
    
    }
}