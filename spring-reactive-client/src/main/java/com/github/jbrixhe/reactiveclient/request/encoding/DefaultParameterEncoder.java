package com.github.jbrixhe.reactiveclient.request.encoding;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DefaultParameterEncoder implements ParameterEncoder{
    private ConversionService conversionService;

    public DefaultParameterEncoder() {
        this.conversionService = new DefaultConversionService();
    }

    public Map<String, String> encodeToString(Map<Integer, String> indexToName, Object[] parameterValues) {
        Map<String, String> encodedParameter = new HashMap<>();
        for (Map.Entry<Integer, String> mapEntry : indexToName.entrySet()) {
            Object value = parameterValues[mapEntry.getKey()];
            String valueAsString = conversionService.convert(value, String.class);
            encodedParameter.put(mapEntry.getValue(), valueAsString);
        }
        return encodedParameter;
    }

    public Map<String, List<String>> encodeToListOfString(Map<Integer, String> indexToName, Object[] parameterValues) {
        Map<String, List<String>> encodedParameter = new HashMap<>();
        for (Map.Entry<Integer, String> mapEntry : indexToName.entrySet()) {
            Object value = parameterValues[mapEntry.getKey()];
            List<String> valueAsString = encodeParameterValue(value);
            encodedParameter.put(mapEntry.getValue(), valueAsString);
        }
        return encodedParameter;
    }

    private List<String> encodeParameterValue(Object value) {
        if (value == null) {
            return Collections.emptyList();
        } else if (Iterable.class.isInstance(value)) {
            List<String> valuesAsString = new ArrayList<>();
            for (Object o : (Iterable<?>) value) {
                encodeCollectionOrArrayElement(valuesAsString, o);
            }
            return valuesAsString;
        } else if (value.getClass().isArray()) {
            List<String> valuesAsString = new ArrayList<>();
            for (Object o : (Object[]) value) {
                encodeCollectionOrArrayElement(valuesAsString, o);
            }
            return valuesAsString;
        } else {
            String valueAsString = conversionService.convert(value, String.class);
            return Collections.singletonList(valueAsString);
        }
    }

    private void encodeCollectionOrArrayElement(Collection<String> valuesAsString, Object value) {
        if (value == null) {
            return;
        }
        valuesAsString.add(convertToString(value));
    }

    protected String convertToString(Object value) {
        return conversionService.convert(value, String.class);
    }
}