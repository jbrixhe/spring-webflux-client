package com.github.jbrixhe.reactiveclient.metadata.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class RequestTemplate {
    private HttpMethod httpMethod;
    private RequestSegments requestSegments;
    private RequestParameters requestParameters;
    private RequestHeaders requestHeaders;

    public Request apply(Object[] args){
        String path = requestSegments.resolve(args);
        String requestParameter = requestParameters.resolve(args);
        HttpHeaders httpHeaders = requestHeaders.encode(args);
        URI uri = null;
        try {
            uri = new URI(path+requestParameter);
        } catch (URISyntaxException e) {
            throw new RuntimeException();
        }
        return Request.create(uri, httpHeaders, httpMethod);
    }
}
