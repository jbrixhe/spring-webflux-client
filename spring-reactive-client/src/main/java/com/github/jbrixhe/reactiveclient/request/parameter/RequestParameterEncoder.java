package com.github.jbrixhe.reactiveclient.request.parameter;

import org.springframework.core.convert.ConversionService;

class RequestParameterEncoder {
    private StringBuilder stringBuilder;
    private ConversionService conversionService;

    RequestParameterEncoder(ConversionService conversionService) {
        this.conversionService = conversionService;
        this.stringBuilder = new StringBuilder();
    }

    void encode(String parameterName, Object value){
        if (value != null) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("&");
            }

            stringBuilder.append(parameterName)
                    .append("=")
                    .append(conversionService.convert(value, String.class));
        }
    }

    String encodedValue(){
        return stringBuilder.toString();
    }
}
