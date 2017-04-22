package com.reactiveclient.metadata.request.encoding;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DefaultParameterEncoder implements ParameterEncoder {
    private ConversionService conversionService;

    public DefaultParameterEncoder() {
        this.conversionService = new DefaultConversionService();
    }

    public Map<String, String> convertToString(Map<Integer, String> indexToName, Object[] values) {
        Map<String, String> encodedParameter = new HashMap<>();
        for (Map.Entry<Integer, String> mapEntry : indexToName.entrySet()) {
            Object value = values[mapEntry.getKey()];
            String valueAsString = convertToString(value);
            encodedParameter.put(mapEntry.getValue(), valueAsString);
        }
        return encodedParameter;
    }

    public Map<String, List<String>> convertToListOfString(Map<Integer, String> indexToName, Object[] values) {
        Map<String, List<String>> encodedParameter = new HashMap<>();
        for (Map.Entry<Integer, String> mapEntry : indexToName.entrySet()) {
            Object value = values[mapEntry.getKey()];
            List<String> valueAsString = processValue(value);
            encodedParameter.put(mapEntry.getValue(), valueAsString);
        }
        return encodedParameter;
    }

    List<String> processValue(Object value) {
        if (value == null) {
            return Collections.emptyList();
        } else if (Iterable.class.isInstance(value)) {
            List<String> valuesAsString = new ArrayList<>();
            for (Object o : (Iterable<?>) value) {
                addCollectionOrArrayElement(valuesAsString, o);
            }
            return valuesAsString;
        } else if (value.getClass().isArray()) {
            List<String> valuesAsString = new ArrayList<>();
            for (Object o : (Object[]) value) {
                addCollectionOrArrayElement(valuesAsString, o);
            }
            return valuesAsString;
        } else {
            String valueAsString = convertToString(value);
            return Collections.singletonList(valueAsString);
        }
    }

    private void addCollectionOrArrayElement(Collection<String> valuesAsString, Object value) {
        if (value == null) {
            return;
        }
        valuesAsString.add(convertToString(value));
    }

    protected String convertToString(Object value) {
        return String.class.isInstance(value) ?
                (String) value :
                conversionService.convert(value, String.class);
    }
}