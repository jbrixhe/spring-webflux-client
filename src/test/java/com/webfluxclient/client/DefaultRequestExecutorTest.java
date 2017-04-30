package com.webfluxclient.client;


import com.webfluxclient.metadata.request.Request;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultRequestExecutorTest {
    @Mock
    private ExchangeFunction exchangeFunction;
    @Captor
    private ArgumentCaptor<ClientRequest> captor;
    private DefaultRequestExecutor requestExecutor;
    @Before
    public void setup() throws Exception {
        requestExecutor = create();
        when(this.exchangeFunction.exchange(captor.capture())).thenReturn(Mono.empty());
    }
    
    
    @Test
    public void execute() {
        Request request = new MockRequest("http://example.ca", HttpMethod.GET);
        request.headers().add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        
        requestExecutor.execute(request);
        ClientRequest clientRequest = verifyExchange();
        assertThat(clientRequest.url())
                .isEqualTo(URI.create("http://example.ca"));
        assertThat(clientRequest.method())
                .isEqualTo(HttpMethod.GET);
    }
    
    @Test
    public void execute_withHeaders() {
        Request request = new MockRequest("http://example.ca", HttpMethod.GET);
        request.headers().add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        request.headers().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE);
        
        requestExecutor.execute(request);
        ClientRequest clientRequest = verifyExchange();
        assertThat(clientRequest.url())
                .isEqualTo(URI.create("http://example.ca"));
        assertThat(clientRequest.method())
                .isEqualTo(HttpMethod.GET);
        assertThat(clientRequest.headers().getFirst(HttpHeaders.ACCEPT))
                .isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(clientRequest.headers().getFirst(HttpHeaders.CONTENT_TYPE))
                .isEqualTo(MediaType.APPLICATION_XML_VALUE);
    }
    
    private DefaultRequestExecutor create() {
        WebClient webClient = WebClient.builder().baseUrl("/base").exchangeFunction(this.exchangeFunction).build();
        return new DefaultRequestExecutor(webClient);
    }
    
    private ClientRequest verifyExchange() {
        ClientRequest request = this.captor.getValue();
        verify(this.exchangeFunction).exchange(request);
        verifyNoMoreInteractions(this.exchangeFunction);
        return request;
    }
}