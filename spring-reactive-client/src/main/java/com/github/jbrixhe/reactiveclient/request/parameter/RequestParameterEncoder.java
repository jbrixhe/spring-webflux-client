package com.github.jbrixhe.reactiveclient.request.parameter;

import org.springframework.core.convert.ConversionService;

class RequestParameterEncoder {
    private StringBuilder stringBuilder;
    private ConversionService conversionService;

    RequestParameterEncoder(ConversionService conversionService) {
        this.conversionService = conversionService;
        this.stringBuilder = new StringBuilder();
    }

    void encode(String parameterName, Object value) {
        if (value != null) {
            stringBuilder
                    .append("&")
                    .append(parameterName)
                    .append("=")
                    .append(conversionService.convert(value, String.class));
        }
    }

    public String value() {
        return stringBuilder
                .deleteCharAt(0)
                .insert(0, "?")
                .toString();
    }
}
