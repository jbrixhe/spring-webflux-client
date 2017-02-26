package com.reactiveclient.metadata.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class RequestTemplate {
    private HttpMethod httpMethod;
    private RequestHeaders requestHeaders;
    private Integer bodyIndex;
    private UriBuilder uriBuilder;
    private MultiValueMap<Integer, String> variableIndexToName;

    public Request apply(Object[] args) {

        Request request = new Request();
        request.setUri(buildUri(args));
        request.setHttpHeaders(requestHeaders.encode(args));
        request.setHttpMethod(httpMethod);
        request.setBody(buildBody(args));
        return request;
    }

    private URI buildUri(Object[] args) {
        Map<String, Object> nameToVariable = new HashMap<>();
        for (Map.Entry<Integer, List<String>> integerListEntry : variableIndexToName.entrySet()) {
            Object variable = processVariable(args[integerListEntry.getKey()]);
            integerListEntry.getValue().forEach(variableName -> nameToVariable.put(variableName, variable));
        }
        return uriBuilder.build(nameToVariable);
    }

    private Object buildBody(Object[] args) {
        return bodyIndex != null ?
                args[bodyIndex] :
                null;
    }

    private Object processVariable(Object variable) {
        if (Collection.class.isInstance(variable)) {
            return ((Collection<?>) variable).toArray();
        }
        return variable;
    }
}
