package com.github.jbrixhe.reactiveclient.request;

import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class RequestPath {
    private List<Segment> segments;

    public RequestPath() {
        this.segments = new LinkedList<>();
    }

    public void append(String path) {
        if (StringUtils.hasText(path)) {
            processPathSegment(path, segments::add);
        }
    }

    void processPathSegment(String path, Consumer<Segment> segmentConsumer) {
        for (String segment : path.split("/")) {
            if (StringUtils.hasText(segment)) {
                segmentConsumer.accept(Segment.fromString(segment));
            }
        }
    }

}
