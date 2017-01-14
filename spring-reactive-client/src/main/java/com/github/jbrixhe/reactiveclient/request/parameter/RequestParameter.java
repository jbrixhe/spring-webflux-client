package com.github.jbrixhe.reactiveclient.request.parameter;

import java.util.Collection;
import java.util.Map;

public interface RequestParameter {

    void encode(RequestParameterEncoder requestParameterEncoder, Map<String, Object> parameterValues);

    static RequestParameter create(String name, Class<?> parameterType) {
        if (Collection.class.isAssignableFrom(parameterType)) {
            return new CollectionRequestParameter(name);
        } else if (Object[].class.isAssignableFrom(parameterType)) {
            return new ArrayRequestParameter(name);
        } else {
            return new DefaultRequestParameter(name);
        }
    }

    class DefaultRequestParameter implements RequestParameter {

        private String parameterName;

        DefaultRequestParameter(String parameterName) {
            this.parameterName = parameterName;
        }

        @Override
        public void encode(RequestParameterEncoder requestParameterEncoder, Map<String, Object> parameterValues) {
            requestParameterEncoder.encode(parameterName, parameterValues.get(parameterName));
        }
    }

    class CollectionRequestParameter implements RequestParameter {

        private String parameterName;

        CollectionRequestParameter(String parameterName) {
            this.parameterName = parameterName;
        }

        @Override
        public void encode(RequestParameterEncoder requestParameterEncoder, Map<String, Object> parameterValues) {
            Collection<?> values = (Collection<?>) parameterValues.get(parameterName);
            if (values != null) {
                values.forEach(value -> requestParameterEncoder.encode(parameterName, value));
            }
        }
    }

    class ArrayRequestParameter implements RequestParameter {

        private String parameterName;

        ArrayRequestParameter(String parameterName) {
            this.parameterName = parameterName;
        }

        @Override
        public void encode(RequestParameterEncoder requestParameterEncoder, Map<String, Object> parameterValues) {
            Object[] values = (Object[]) parameterValues.get(parameterName);
            if (values != null) {
                for (Object value: values){
                    requestParameterEncoder.encode(parameterName, value);
                }
            }
        }
    }
}
