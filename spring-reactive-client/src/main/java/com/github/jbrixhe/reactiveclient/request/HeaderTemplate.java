package com.github.jbrixhe.reactiveclient.request;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public interface HeaderTemplate {

    Header resolve(Map<String,String> args);

    interface Header {
        String getName();

        Collection<String> getValues();
    }

    class BasicT implements HeaderTemplate {
        private BasicHeader staticH;
        public BasicT(String name, String... values) {
            this.staticH = new BasicHeader(name, values);
        }

        @Override
        public Header resolve(Map<String, String> args) {
            return staticH;
        }
    }

    class BasicHeader implements Header {
        String name;
        Collection<String> values;

        public BasicHeader(String name, String... values) {
            this.name = name;
            this.values = Arrays.asList(values);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Collection<String> getValues() {
            return values;
        }
    }
}
