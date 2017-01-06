package com.github.jbrixhe.reactiveclient.request;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class RequestPathTest {

    @Test
    public void processPathSegment() {
        List<Segment> segments = new LinkedList<>();
        new RequestPath().processPathSegment("/api/{id}", segments::add);
        assertThat(segments)
                .hasSize(2);
    }

    @Test
    public void processPathSegment_withDuplicateSlash() {
        List<Segment> segments = new LinkedList<>();
        new RequestPath().processPathSegment("//{id}", segments::add);
        assertThat(segments)
                .hasSize(1);
    }

    @Test
    public void processPathSegment_withSlashAtTheEnd() {
        List<Segment> segments = new LinkedList<>();
        new RequestPath().processPathSegment("/api/{id}/", segments::add);
        assertThat(segments)
                .hasSize(2);
    }

    @Test
    public void processPathSegment_withEmptySegment() {
        List<Segment> segments = new LinkedList<>();
        new RequestPath().processPathSegment("/  /{id}", segments::add);
        assertThat(segments)
                .hasSize(1);
    }
}