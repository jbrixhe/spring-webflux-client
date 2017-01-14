package com.github.jbrixhe.reactiveclient.request.segment;

import org.springframework.core.convert.ConversionService;

class RequestSegmentEncoder {
    private StringBuilder stringBuilder;
    private ConversionService conversionService;

    RequestSegmentEncoder(ConversionService conversionService) {
        this.conversionService = conversionService;
        this.stringBuilder = new StringBuilder();
    }

    void encode(Object... subSegments) {
        for (Object subSegment: subSegments){
            stringBuilder
                    .append("/")
                    .append(String.class.isInstance(subSegment)? (String)subSegment : conversionService.convert(subSegment, String.class));
        }
    }

    public String value() {
        return stringBuilder.toString();
    }
}
