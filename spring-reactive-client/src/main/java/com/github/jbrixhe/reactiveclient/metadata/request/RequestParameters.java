package com.github.jbrixhe.reactiveclient.metadata.request;

import com.github.jbrixhe.reactiveclient.metadata.request.encoding.ParameterEncoder;

import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

public class RequestParameters {

    private final ParameterEncoder parameterEncoder;
    private final Map<String, RequestParameter> requestParameters;
    private final Map<Integer, String> indexToName;

    public RequestParameters(Map<String, RequestParameter> requestParameters, Map<Integer, String> indexToName) {
        this.parameterEncoder = ParameterEncoder.create(true);
        this.requestParameters = unmodifiableMap(requestParameters);
        this.indexToName = unmodifiableMap(indexToName);
    }

    public Map<String, RequestParameter> getRequestParameters() {
        return requestParameters;
    }

    public Map<Integer, String> getIndexToName() {
        return indexToName;
    }

    public String resolve(Object[] parameters) {
        Map<String, List<String>> headerDynamicValue = parameterEncoder.convertToListOfString(indexToName, parameters);
        RequestParameterAccumulator requestParameterAccumulator = new RequestParameterAccumulator();

        requestParameters
                .values()
                .forEach(requestParameter -> requestParameterAccumulator.add(requestParameter.getName(), requestParameter.getValues(headerDynamicValue)));

        return requestParameterAccumulator.value();
    }

    class RequestParameterAccumulator {
        private StringBuilder stringBuilder;

        RequestParameterAccumulator() {
            this.stringBuilder = new StringBuilder();
        }

        void add(String parameterName, List<String> values) {
            for (String value : values) {
                stringBuilder
                        .append("&")
                        .append(parameterName)
                        .append("=")
                        .append(value);
            }
        }

        public String value() {
            return stringBuilder.length()==0?
                    stringBuilder.toString():
                    stringBuilder
                    .deleteCharAt(0)
                    .insert(0, "?")
                    .toString();
        }
    }
}
