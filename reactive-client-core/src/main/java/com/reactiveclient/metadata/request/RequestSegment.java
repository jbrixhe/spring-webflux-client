package com.reactiveclient.metadata.request;

import java.util.Map;

public interface RequestSegment {

    String getValue(Map<String, String> parameterValues);

    static RequestSegment create(String segment){
        if (segment.startsWith("{") && segment.endsWith("}")) {
            return new DynamicRequestSegment(segment);
        } else {
            return new BasicRequestSegment(segment);
        }
    }

    class BasicRequestSegment implements RequestSegment {

        private String segment;

        private BasicRequestSegment(String segment) {
            this.segment = segment;
        }

        @Override
        public String getValue(Map<String, String> parameterValues) {
            return segment;
        }
    }

    class DynamicRequestSegment implements RequestSegment {

        private String segment;
        private String defaultValue;

        private DynamicRequestSegment(String segment) {
            this.defaultValue = segment;
            this.segment = segment.substring(1, segment.length() - 1);
        }

        @Override
        public String getValue(Map<String, String> parameterValues) {
            return parameterValues.getOrDefault(segment, defaultValue);
        }
    }
}
