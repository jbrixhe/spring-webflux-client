package com.github.jbrixhe.reactiveclient.request;

import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class RequestPath {
    private List<PathSegment> pathSegments;

    public RequestPath() {
        this.pathSegments = new LinkedList<>();
    }

    public void append(String path) {
        if (StringUtils.hasText(path)) {
            for (String segment : path.split("/")) {
                if (StringUtils.hasText(segment)) {
                    pathSegments.add(PathSegment.get(segment));
                }
            }
        }
    }

    List<PathSegment> getPathSegments() {
        return pathSegments;
    }
}
