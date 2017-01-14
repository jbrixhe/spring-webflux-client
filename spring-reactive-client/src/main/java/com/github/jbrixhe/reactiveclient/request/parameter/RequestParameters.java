package com.github.jbrixhe.reactiveclient.request.parameter;

import com.github.jbrixhe.reactiveclient.request.encoding.ParameterEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestParameters {

    private ParameterEncoder parameterEncoder;
    private Map<String, RequestParameter> requestParameters;
    private Map<Integer, String> indexToName;

    public RequestParameters() {
        this.parameterEncoder = ParameterEncoder.create(true);
        this.requestParameters = new HashMap<>();
        this.indexToName = new HashMap<>();
    }

    public void add(String parameterName) {
        requestParameters.put(parameterName, new RequestParameter.DynamicRequestParameter(parameterName));
    }

    public void addIndex(Integer index, String parameterName) {
        this.indexToName.put(index, parameterName);
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
            return stringBuilder
                    .deleteCharAt(0)
                    .insert(0, "?")
                    .toString();
        }
    }
}
