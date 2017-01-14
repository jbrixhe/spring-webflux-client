package com.github.jbrixhe.reactiveclient.request.parameter;

import com.github.jbrixhe.reactiveclient.request.Resolvable;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.HashMap;
import java.util.Map;

public class RequestParameters implements Resolvable {

    Map<String, RequestParameter> requestParameters;
    ConversionService conversionService;

    public RequestParameters(ConversionService conversionService) {
        this.conversionService = conversionService;
        this.requestParameters = new HashMap<>();
    }

    public RequestParameters() {
        this(new DefaultConversionService());
    }

    public void add(String parameterName, Class<?> type) {
        if (!requestParameters.containsKey(parameterName)) {
            requestParameters.put(parameterName, RequestParameter.build(parameterName, type));
        } else {
            throw new IllegalArgumentException("Duplicate Request parameter name:"+parameterName);
        }
    }

    @Override
    public String resolve(Map<String, Object> parameters) {
        RequestParameterEncoder requestParameterEncoder = new RequestParameterEncoder(conversionService);

        requestParameters.values()
                .forEach(requestParameter -> requestParameter.encode(requestParameterEncoder, parameters));

        return requestParameterEncoder.encodedValue();
    }
}
