package com.reactiveclient.metadata.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class RequestTemplate {
    private UriBuilder uriBuilder;
    private HttpMethod httpMethod;
    private RequestHeaders requestHeaders;
    private Integer bodyIndex;
    private MultiValueMap<Integer, String> variableIndexToName;

    public ClientRequest apply(Object[] args) {
        return new ClientRequest(uriBuilder,
                httpMethod,
                requestHeaders.encode(args),
                nameToVariable(args),
                buildBody(args));
    }

    private Map<String, Object> nameToVariable(Object[] args) {
        Map<String, Object> nameToVariable = new HashMap<>();
        for (Map.Entry<Integer, List<String>> integerListEntry : variableIndexToName.entrySet()) {
            Object variable = processVariable(args[integerListEntry.getKey()]);
            integerListEntry.getValue().forEach(variableName -> nameToVariable.put(variableName, variable));
        }
        return nameToVariable;
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
