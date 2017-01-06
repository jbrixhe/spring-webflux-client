package com.github.jbrixhe.reactiveclient.request;

import java.util.Map;

interface Segment {
    String resolve(Map<String,String> parameters);

    static Segment fromString(String segment){
        if (segment.startsWith("{") && segment.endsWith("}")) {
            return new DynamicSegment(segment);
        } else {
            return new StaticSegment(segment);
        }
    }

    class StaticSegment implements Segment {

        private String segment;

        public StaticSegment(String segment) {
            this.segment = segment;
        }

        @Override
        public String resolve(Map<String,String> parameters) {
            return segment;
        }
    }

    class DynamicSegment implements Segment {

        private String segment;
        private String defaultValue;

        public DynamicSegment(String segment) {
            this.defaultValue = segment;
            this.segment = segment.substring(1, segment.length() - 1);
        }

        @Override
        public String resolve(Map<String, String> parameters) {
            return parameters.getOrDefault(segment, defaultValue);
        }
    }
}
