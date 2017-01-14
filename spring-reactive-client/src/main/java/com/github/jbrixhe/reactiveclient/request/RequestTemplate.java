package com.github.jbrixhe.reactiveclient.request;

import com.github.jbrixhe.reactiveclient.request.parameter.RequestParameters;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class RequestTemplate {
    Map<Integer, String> indexToParameterNames;
    Map<String, HeaderTemplate> headerTemplates;
    RequestPath requestPath;
    RequestParameters requestParameters;
    HttpMethod method;

    public RequestTemplate(RequestTemplate requestTemplate) {
        this();
        this.requestPath.getPathSegments().addAll(requestTemplate.requestPath.getPathSegments());
        this.headerTemplates.putAll(requestTemplate.headerTemplates);
        this.method = requestTemplate.method;
        this.indexToParameterNames = new HashMap<>();
    }

    public RequestTemplate() {
        this.requestPath = new RequestPath();
        this.headerTemplates = new HashMap<>();
        this.requestParameters = new RequestParameters();
    }

    public RequestPath getRequestPath() {
        return requestPath;
    }

    public void addHeader(String name, String value) {
        if (!headerTemplates.containsKey(name)) {
            headerTemplates.put(name, new HeaderTemplate.BasicT(name, value));
        }
    }

    public void addRequestParameter(String name, Class<?> parameterType) {
        requestParameters.add(name, parameterType);
    }

    public void setParameterName(String name, Integer index) {
        indexToParameterNames.put(index, name);
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }
}
