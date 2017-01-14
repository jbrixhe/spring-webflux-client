package com.github.jbrixhe.reactiveclient.request;

import java.util.Map;

interface PathSegment extends Resolvable{

    static PathSegment get(String segment){
        if (segment.startsWith("{") && segment.endsWith("}")) {
            return new DynamicPathSegment(segment);
        } else {
            return new StaticPathSegment(segment);
        }
    }

    class StaticPathSegment implements PathSegment {

        private String segment;

        public StaticPathSegment(String segment) {
            this.segment = segment;
        }

        @Override
        public String resolve(Map<String,Object> parameters) {
            return segment;
        }
    }

    class DynamicPathSegment implements PathSegment {

        private String segment;
        private String defaultValue;

        public DynamicPathSegment(String segment) {
            this.defaultValue = segment;
            this.segment = segment.substring(1, segment.length() - 1);
        }

        @Override
        public String resolve(Map<String, Object> parameters) {
            return String.valueOf(parameters.getOrDefault(segment, defaultValue));
        }
    }
}
