package com.github.jbrixhe.reactiveclient.request;

import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RequestTemplate {
    private HttpMethod httpMethod;
    private RequestSegments requestSegments;
    private RequestParameters requestParameters;
    private RequestHeaders requestHeaders;
    private Method targetMethod;

    private RequestTemplate(Builder builder) {
        requestSegments = new RequestSegments(builder.requestSegments, builder.segmentIndexToName);
        requestParameters = new RequestParameters(builder.requestParameters, builder.requestParameterIndexToName);
        requestHeaders = new RequestHeaders(builder.headers, builder.headerIndexToName);
        httpMethod = builder.httpMethod;
        targetMethod = builder.targetMethod;
    }

    static Builder newBuilder() {
        return new Builder();
    }

    static Builder newBuilder(RequestTemplate other) {
        return new Builder(other);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestSegments getRequestSegments() {
        return requestSegments;
    }

    public RequestParameters getRequestParameters() {
        return requestParameters;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public static class Builder {
        private List<RequestSegment> requestSegments;
        private Map<Integer, String> segmentIndexToName;
        private Map<String, RequestParameter> requestParameters;
        private Map<Integer, String> requestParameterIndexToName;
        private Map<String, RequestHeader> headers;
        private Map<Integer, String> headerIndexToName;
        private HttpMethod httpMethod;
        private Method targetMethod;

        public Builder() {
            requestSegments = new LinkedList<>();
            segmentIndexToName = new HashMap<>();
            requestParameters = new HashMap<>();
            requestParameterIndexToName = new HashMap<>();
            headers = new HashMap<>();
            headerIndexToName = new HashMap<>();

        }

        public Builder(RequestTemplate other){
            this();
            targetMethod = other.getTargetMethod();
            requestSegments.addAll(other.getRequestSegments().getRequestSegments());
            segmentIndexToName.putAll(other.getRequestSegments().getIndexToName());
            requestParameters.putAll(other.getRequestParameters().getRequestParameters());
            requestParameterIndexToName.putAll(other.getRequestParameters().getIndexToName());
            headers.putAll(other.getRequestHeaders().getHeaders());
            headerIndexToName.putAll(other.getRequestHeaders().getIndexToName());
            httpMethod = other.httpMethod;
        }

        public Builder addPath(String path) {
            for (String segment : path.split("/")) {
                if (StringUtils.hasText(segment)) {
                    requestSegments.add(RequestSegment.create(segment));
                }
            }
            return this;
        }

        public Builder addPathIndex(Integer index, String pathVariable) {
            this.segmentIndexToName.put(index, pathVariable);
            return this;
        }

        public Builder addHeader(String name, String value) {
            headers.put(name, new RequestHeader.BasicRequestHeader(name, value));
            return this;
        }

        public Builder addHeader(Integer index, String name) {
            headers.put(name, new RequestHeader.DynamicRequestHeader(name));
            headerIndexToName.put(index, name);
            return this;
        }

        public Builder addParameter(Integer index, String name) {
            requestParameters.put(name, new RequestParameter.DynamicRequestParameter(name));
            requestParameterIndexToName.put(index, name);
            return this;
        }

        public Builder httpMethod(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder targetMethod(Method targetMethod){
            this.targetMethod = targetMethod;
            return this;
        }

        public RequestTemplate build() {
            return new RequestTemplate(this);
        }
    }
}
