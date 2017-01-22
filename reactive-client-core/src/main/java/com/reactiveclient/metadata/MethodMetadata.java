package com.reactiveclient.metadata;

import com.reactiveclient.metadata.request.RequestHeader;
import com.reactiveclient.metadata.request.RequestHeaders;
import com.reactiveclient.metadata.request.RequestParameter;
import com.reactiveclient.metadata.request.RequestParameters;
import com.reactiveclient.metadata.request.RequestSegment;
import com.reactiveclient.metadata.request.RequestSegments;
import com.reactiveclient.metadata.request.RequestTemplate;
import lombok.Getter;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
public class MethodMetadata {
    private RequestTemplate requestTemplate;
    private Method targetMethod;
    private ReturnType returnType;

    private MethodMetadata(Builder builder) {
        targetMethod = builder.targetMethod;
        returnType = builder.returnType;
        requestTemplate = new RequestTemplate(builder.httpMethod,
                new RequestSegments(builder.requestSegments, builder.segmentIndexToName),
                new RequestParameters(builder.requestParameters, builder.requestParameterIndexToName),
                new RequestHeaders(builder.headers, builder.headerIndexToName),
                builder.targetHost);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(MethodMetadata other) {
        return new Builder(other);
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
        private ReturnType returnType;
        private String targetHost;

        public Builder() {
            requestSegments = new LinkedList<>();
            segmentIndexToName = new HashMap<>();
            requestParameters = new HashMap<>();
            requestParameterIndexToName = new HashMap<>();
            headers = new HashMap<>();
            headerIndexToName = new HashMap<>();

        }

        public Builder(MethodMetadata other) {
            this();
            requestSegments.addAll(other.getRequestTemplate().getRequestSegments().getRequestSegments());
            segmentIndexToName.putAll(other.getRequestTemplate().getRequestSegments().getIndexToName());
            requestParameters.putAll(other.getRequestTemplate().getRequestParameters().getRequestParameters());
            requestParameterIndexToName.putAll(other.getRequestTemplate().getRequestParameters().getIndexToName());
            headers.putAll(other.getRequestTemplate().getRequestHeaders().getHeaders());
            headerIndexToName.putAll(other.getRequestTemplate().getRequestHeaders().getIndexToName());
            httpMethod = other.getRequestTemplate().getHttpMethod();
            targetHost = other.getRequestTemplate().getTargetHost();
            targetMethod = other.getTargetMethod();
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

        public Builder targetMethod(Method targetMethod) {
            this.targetMethod = targetMethod;
            return this;
        }

        public Builder targetHost(String scheme, String authority) {
            targetHost = scheme + "://" + authority;
            return this;
        }

        public Builder returnType(ReturnType returnType) {
            this.returnType = returnType;
            return this;
        }

        public MethodMetadata build() {
            return new MethodMetadata(this);
        }
    }
}
