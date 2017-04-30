package com.webfluxclient.metadata.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.util.UriBuilder;

import java.util.HashMap;
import java.util.Map;

import static com.webfluxclient.utils.Types.*;

@Getter
@AllArgsConstructor
public class RequestTemplate {
    private UriBuilder uriBuilder;
    private HttpMethod httpMethod;
    private RequestHeaders requestHeaders;
    private Integer bodyIndex;
    private ResolvableType requestBodyType;
    private MultiValueMap<Integer, String> variableIndexToName;

    public Request apply(Object[] args) {
        return new DefaultRequest(uriBuilder,
                httpMethod,
                requestHeaders.encode(args),
                nameToVariable(args),
                buildBody(args));
    }

    private BodyInserter<?, ? super ClientHttpRequest> buildBody(Object[] args) {
        if (bodyIndex == null) {
            return BodyInserters.empty();
        }
        
        Object body = args[bodyIndex];
        if (isDataBufferPublisher(requestBodyType)) {
            return BodyInserters.fromDataBuffers((Publisher<DataBuffer>) body);
        } else if (isPublisher(requestBodyType)) {
            return BodyInserters.fromPublisher((Publisher) body, requestBodyType.getGeneric(0));
        } else if (isResource(requestBodyType)) {
            return BodyInserters.fromResource((Resource) body);
        } else if (isFormData(requestBodyType)) {
            return BodyInserters.fromFormData((MultiValueMap<String, String>) body);
        } else {
            return BodyInserters.fromObject(body);
        }
    }
    
    private Map<String, Object> nameToVariable(Object[] args) {
        Map<String, Object> nameToVariable = new HashMap<>();
        variableIndexToName.forEach((variableIndex, variableNames) -> {
            variableNames
                    .forEach(variableName -> nameToVariable.put(variableName, args[variableIndex]));
        });
        return nameToVariable;
    }
}
