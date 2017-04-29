package com.webfluxclient.metadata.request;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@EqualsAndHashCode
@AllArgsConstructor
public class Request {
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
    @Getter
    private BodyInserter<?, ? super ClientHttpRequest> bodyInserter;

    public URI expand() {
        return uriBuilder.build(variables);
    }

    public List<String> header(String header) {
        return httpHeaders.get(header);
    }

    public void addHeader(String header, String... values) {
        addHeader(header, asList(values));
    }

    public void addHeader(String header, List<String> values) {
        httpHeaders.put(header, values);
    }
}
