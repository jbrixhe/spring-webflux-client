package com.github.jbrixhe.reactiveclient.request.header;

import com.github.jbrixhe.reactiveclient.request.encoding.ParameterEncoder;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaders {

    private ParameterEncoder parameterEncoder;
    private Map<String, RequestHeader> headers;
    private Map<Integer, String> indexToName;

    public RequestHeaders() {
        this.parameterEncoder = ParameterEncoder.create(false);
        this.headers = new LinkedCaseInsensitiveMap<>();
        this.indexToName = new HashMap<>();
    }

    public void add(String name, String value) {
        headers.put(name, new RequestHeader.BasicRequestHeader(name, value));
    }

    public void add(String name) {
        headers.put(name, new RequestHeader.DynamicRequestHeader(name));
    }

    public void addIndex(Integer index, String parameterName) {
        indexToName.put(index, parameterName);
    }

    public HttpHeaders encode(Object[] parameterValues) {
        Map<String, List<String>> headerDynamicValue = parameterEncoder.encodeToListOfString(indexToName, parameterValues);
        HttpHeaders httpHeaders = new HttpHeaders();
        for (RequestHeader header : headers.values()) {
            List<String> headerValues = header.getValues(headerDynamicValue);
            if (!headerValues.isEmpty()) {
                httpHeaders.put(header.getName(), headerValues);
            }
        }
        return httpHeaders;
    }
}
