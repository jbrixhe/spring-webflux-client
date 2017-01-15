package com.github.jbrixhe.reactiveclient.request;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class RequestSegmentsTest {

    @Test
    public void resolve_withBasicSegment() {
        List<RequestSegment> segmentMap = new LinkedList<>();
        segmentMap.add(RequestSegment.create("api"));
        segmentMap.add(RequestSegment.create("user"));
        RequestSegments requestSegments = new RequestSegments(segmentMap, emptyMap());
        assertThat(requestSegments.resolve(new Object[]{}))
                .isEqualTo("/api/user");
    }

    @Test
    public void resolve_withDynamicSegment() {
        List<RequestSegment> segmentMap = new LinkedList<>();
        segmentMap.add(RequestSegment.create("api"));
        segmentMap.add(RequestSegment.create("user"));
        segmentMap.add(RequestSegment.create("{id}"));
        Map<Integer, String> indexToname = new HashMap<>();
        indexToname.put(0, "id");
        RequestSegments requestSegments = new RequestSegments(segmentMap, indexToname);
        assertThat(requestSegments.resolve(new Object[]{123}))
                .isEqualTo("/api/user/123");
    }

    @Test
    public void resolve_withNullValue() {
        List<RequestSegment> segmentMap = new LinkedList<>();
        segmentMap.add(RequestSegment.create("api"));
        segmentMap.add(RequestSegment.create("user"));
        segmentMap.add(RequestSegment.create("{id}"));
        Map<Integer, String> indexToname = new HashMap<>();
        indexToname.put(0, "id");
        RequestSegments requestSegments = new RequestSegments(segmentMap, indexToname);
        assertThat(requestSegments.resolve(new Object[]{null}))
                .isEqualTo("/api/user/null");
    }

    @Test
    public void resolve_withNoValue() {
        List<RequestSegment> segmentMap = new LinkedList<>();
        segmentMap.add(RequestSegment.create("api"));
        segmentMap.add(RequestSegment.create("user"));
        segmentMap.add(RequestSegment.create("{id}"));
        RequestSegments requestSegments = new RequestSegments(segmentMap, emptyMap());
        assertThat(requestSegments.resolve(new Object[]{123}))
                .isEqualTo("/api/user/{id}");
    }
}