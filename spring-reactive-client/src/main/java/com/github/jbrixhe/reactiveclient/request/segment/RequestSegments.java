package com.github.jbrixhe.reactiveclient.request.segment;

import com.github.jbrixhe.reactiveclient.request.Resolvable;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RequestSegments implements Resolvable{
    private final ConversionService conversionService;
    private List<RequestSegment> requestSegments;

    public RequestSegments(ConversionService conversionService) {
        this.conversionService = conversionService;
        this.requestSegments = new LinkedList<>();
    }

    public RequestSegments() {
        this(new DefaultConversionService());
    }

    public void add(String path) {
        for (String segment : path.split("/")) {
            if (StringUtils.hasText(segment)) {
                requestSegments.add(RequestSegment.create(segment));
            }
        }
    }


    @Override
    public String resolve(Map<String, Object> parameters) {
        RequestSegmentEncoder requestSegmentEncoder = new RequestSegmentEncoder(conversionService);

        requestSegments.forEach(requestSegment -> requestSegment.encode(requestSegmentEncoder, parameters));

        return requestSegmentEncoder.value();
    }
}
