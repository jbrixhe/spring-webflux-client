package com.github.jbrixhe.reactiveclient.request;

import com.github.jbrixhe.reactiveclient.request.encoding.ParameterEncoder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

public class RequestSegments {
    private ParameterEncoder parameterEncoder;
    private List<RequestSegment> requestSegments;
    private Map<Integer, String> indexToName;

    public RequestSegments(List<RequestSegment> requestSegments, Map<Integer, String> indexToName) {
        this.parameterEncoder = ParameterEncoder.create(true);
        this.requestSegments = unmodifiableList(requestSegments);
        this.indexToName = unmodifiableMap(indexToName);
    }

    public List<RequestSegment> getRequestSegments() {
        return requestSegments;
    }

    public Map<Integer, String> getIndexToName() {
        return indexToName;
    }

    public String resolve(Object[] parameters) {
        Map<String, String> parameterEncodedValues = parameterEncoder.convertToString(indexToName, parameters);
        return requestSegments.stream()
                .map(segment -> segment.getValue(parameterEncodedValues))
                .collect(Collectors.joining("/", "/", ""));
    }
}
