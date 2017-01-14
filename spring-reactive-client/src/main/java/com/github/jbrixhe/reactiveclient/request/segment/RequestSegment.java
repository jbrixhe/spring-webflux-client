package com.github.jbrixhe.reactiveclient.request.segment;

import java.util.Map;

interface RequestSegment {

    void encode(RequestSegmentEncoder requestSegmentEncoder, Map<String, Object> parameterValues);

    static RequestSegment create(String segment){
        if (segment.startsWith("{") && segment.endsWith("}")) {
            return new DynamicPathRequestSegment(segment);
        } else {
            return new BasicPathRequestSegment(segment);
        }
    }

    class BasicPathRequestSegment implements RequestSegment {

        private String segment;

        private BasicPathRequestSegment(String segment) {
            this.segment = segment;
        }

        @Override
        public void encode(RequestSegmentEncoder requestSegmentEncoder, Map<String, Object> parameterValues) {
            requestSegmentEncoder.encode(segment);
        }
    }

    class DynamicPathRequestSegment implements RequestSegment {

        private String segment;
        private String defaultValue;

        private DynamicPathRequestSegment(String segment) {
            this.defaultValue = segment;
            this.segment = segment.substring(1, segment.length() - 1);
        }

        @Override
        public void encode(RequestSegmentEncoder requestSegmentEncoder, Map<String, Object> parameterValues) {
            requestSegmentEncoder.encode(parameterValues.getOrDefault(segment, defaultValue));
        }
    }
}
