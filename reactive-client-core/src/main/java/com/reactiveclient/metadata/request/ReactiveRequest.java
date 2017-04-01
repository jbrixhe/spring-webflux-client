package com.reactiveclient.metadata.request;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.Map;

@EqualsAndHashCode
@AllArgsConstructor
public class ReactiveRequest {
    private UriBuilder uriBuilder;
    @Setter
    @Getter
    private HttpMethod httpMethod;
    @Setter
    @Getter
    private HttpHeaders httpHeaders;
    @Setter
    @Getter
    private Map<String, Object> variables;
    @Setter
    @Getter
    private Object body;

    public URI expand(){
        return uriBuilder.build(variables);
    }
}
