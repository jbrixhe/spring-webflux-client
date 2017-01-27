package com.reactiveclient.metadata.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

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
    private Integer bodyIndex;

    public Request apply(Object[] args) {

        Request request = new Request();
        request.setUri(buildUri(args));
        request.setHttpHeaders(requestHeaders.encode(args));
        request.setHttpMethod(httpMethod);
        request.setBody(buildBody(args));
        return request;
    }

    private URI buildUri(Object[] args){
        String path = requestSegments.resolve(args);
        String requestParameter = requestParameters.resolve(args);
        try {
            return new URI(targetHost + path + requestParameter);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Object buildBody(Object[] args) {
        return bodyIndex != null?
                args[bodyIndex]:
                null;
    }
}
