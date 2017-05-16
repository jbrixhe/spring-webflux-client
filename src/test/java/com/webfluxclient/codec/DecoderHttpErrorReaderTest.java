package com.webfluxclient.codec;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.mock.http.client.reactive.MockClientHttpResponse;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DecoderHttpErrorReaderTest {
    
    @Mock
    private ErrorDecoder errorDecoder;
    
    @InjectMocks
    private DecoderHttpErrorReader errorReader;
    
    @Test
    public void canRead(){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        when(errorDecoder.canDecode(same(status))).thenReturn(true);
    
        assertThat(errorReader.canRead(status))
                .isTrue();
        
        verify(errorDecoder).canDecode(same(status));
        verifyNoMoreInteractions(errorDecoder);
    }
    
    @Test
    public void readMono(){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ClientHttpResponse httpResponse = request(status, "Exception Mono error message");
        RuntimeException exception = new HttpServerException(status);
        when(errorDecoder.decode(same(status), any(DataBuffer.class))).thenReturn(exception);
    
        StepVerifier.create(errorReader.readMono(httpResponse))
                .verifyError(HttpServerException.class);
        
        verify(errorDecoder).decode(same(status), any(DataBuffer.class));
        verifyNoMoreInteractions(errorDecoder);
    }
    
    @Test
    public void read(){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ClientHttpResponse httpResponse = request(status, "Exception Flux error message");
        RuntimeException exception = new HttpServerException(status);
        when(errorDecoder.decode(same(status), any(DataBuffer.class))).thenReturn(exception);
        
        StepVerifier.create(errorReader.read(httpResponse))
                .verifyError(HttpServerException.class);
        
        verify(errorDecoder).decode(same(status), any(DataBuffer.class));
        verifyNoMoreInteractions(errorDecoder);
    }
    
    @Test
    public void read_withEmpty(){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ClientHttpResponse httpResponse = new MockClientHttpResponse(status);
        
        StepVerifier.create(errorReader.read(httpResponse))
                .expectComplete()
                .verify();
        
        verifyZeroInteractions(errorDecoder);
    }
    
    private ClientHttpResponse request(HttpStatus status, String body) {
        MockClientHttpResponse mockClientHttpResponse = new MockClientHttpResponse(status);
        mockClientHttpResponse.setBody(body);
        return mockClientHttpResponse;
    }
}