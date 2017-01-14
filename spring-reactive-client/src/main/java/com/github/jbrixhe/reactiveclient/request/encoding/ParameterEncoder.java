package com.github.jbrixhe.reactiveclient.request.encoding;

import java.util.List;
import java.util.Map;


public interface ParameterEncoder {

    Map<String, String> encodeToString(Map<Integer, String> indexToName, Object[] parameterValues);

    Map<String, List<String>> encodeToListOfString(Map<Integer, String> indexToName, Object[] parameterValues);

    static ParameterEncoder create(Boolean urlCompatible) {
        return urlCompatible ? new UrlParameterEncoder() : new DefaultParameterEncoder();
    }
}
