package com.github.jbrixhe.reactiveclient.request.header;

import com.github.jbrixhe.reactiveclient.request.encoding.ParameterEncoder;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

public class RequestHeaders {
    private ParameterEncoder parameterEncoder;
    private Map<String, RequestHeader> headers;
    private Map<Integer, String> indexToName;

    public RequestHeaders(Map<String, RequestHeader> headers, Map<Integer, String> indexToName) {
        this.parameterEncoder = ParameterEncoder.create(false);
        this.headers = unmodifiableMap(headers);
        this.indexToName = unmodifiableMap(indexToName);
    }

    public Map<String, RequestHeader> getHeaders() {
        return headers;
    }

    public Map<Integer, String> getIndexToName() {
        return indexToName;
    }

    public HttpHeaders encode(Object[] parameterValues) {
        Map<String, List<String>> headerDynamicValue = parameterEncoder.convertToListOfString(indexToName, parameterValues);
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
