package com.reactiveclient.metadata.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.net.URISyntaxException;

@Getter
@AllArgsConstructor
public class RequestTemplate {
    private HttpMethod httpMethod;
    private RequestSegments requestSegments;
    private RequestParameters requestParameters;
    private RequestHeaders requestHeaders;
    private String targetHost;

    public Request apply(Object[] args) {
        String path = requestSegments.resolve(args);
        String requestParameter = requestParameters.resolve(args);
        HttpHeaders httpHeaders = requestHeaders.encode(args);
        URI uri = null;
        try {
            uri = new URI(targetHost + path + requestParameter);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return Request.create(uri, httpHeaders, httpMethod);
    }
}
