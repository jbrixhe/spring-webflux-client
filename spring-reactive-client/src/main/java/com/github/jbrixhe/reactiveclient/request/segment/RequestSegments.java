package com.github.jbrixhe.reactiveclient.request.segment;

import com.github.jbrixhe.reactiveclient.request.encoding.ParameterEncoder;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestSegments {
    private final ParameterEncoder parameterEncoder;
    private List<RequestSegment> requestSegments;
    private Map<Integer, String> indexToName;

    public RequestSegments() {
        this.parameterEncoder = ParameterEncoder.create(true);
        this.requestSegments = new LinkedList<>();
        this.indexToName = new HashMap<>();
    }

    public void add(String path) {
        for (String segment : path.split("/")) {
            if (StringUtils.hasText(segment)) {
                requestSegments.add(RequestSegment.create(segment));
            }
        }
    }

    public void addIndex(Integer index, String parameterName) {
        this.indexToName.put(index, parameterName);
    }

    public String resolve(Object[] parameters) {
        Map<String, String> parameterEncodedValues = parameterEncoder.convertToString(indexToName, parameters);
        return requestSegments.stream()
                .map(segment -> segment.getValue(parameterEncodedValues))
                .collect(Collectors.joining("/", "/", ""));
    }
}
