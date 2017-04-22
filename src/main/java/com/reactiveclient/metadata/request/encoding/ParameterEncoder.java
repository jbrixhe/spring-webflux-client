package com.reactiveclient.metadata.request.encoding;

import java.util.List;
import java.util.Map;


public interface ParameterEncoder {

    Map<String, String> convertToString(Map<Integer, String> indexToName, Object[] parameterValues);

    Map<String, List<String>> convertToListOfString(Map<Integer, String> indexToName, Object[] parameterValues);

    static ParameterEncoder create(Boolean urlCompatible) {
        return urlCompatible ? new UrlParameterEncoder() : new DefaultParameterEncoder();
    }
}
