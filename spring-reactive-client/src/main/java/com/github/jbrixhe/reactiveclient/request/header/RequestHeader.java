package com.github.jbrixhe.reactiveclient.request.header;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface RequestHeader {
    String getName();

    List<String> getValues(Map<String, List<String>> parameterValues);

    class BasicRequestHeader implements RequestHeader {

        private String name;
        private String value;

        public BasicRequestHeader(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<String> getValues(Map<String, List<String>> parameterValues) {
            return Collections.singletonList(value);
        }
    }

    class DynamicRequestHeader implements RequestHeader {

        private String name;

        public DynamicRequestHeader(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<String> getValues(Map<String, List<String>> parameterValues) {
            return parameterValues.getOrDefault(name, Collections.emptyList());
        }
    }
}
