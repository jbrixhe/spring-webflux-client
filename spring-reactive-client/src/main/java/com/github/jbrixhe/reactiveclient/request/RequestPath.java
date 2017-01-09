package com.github.jbrixhe.reactiveclient.request;

import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class RequestPath {
    private List<Segment> segments;

    public RequestPath(RequestPath requestPath) {
        this();
        segments.addAll(requestPath.getSegments());
    }

    public RequestPath() {
        this.segments = new LinkedList<>();
    }

    public void append(String path) {
        if (StringUtils.hasText(path)) {
            for (String segment : path.split("/")) {
                if (StringUtils.hasText(segment)) {
                    segments.add(Segment.fromString(segment));
                }
            }
        }
    }

    List<Segment> getSegments() {
        return segments;
    }
}
