package com.github.jbrixhe.reactiveclient.request.segment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class RequestSegmentsTest {

    @Test
    public void segmentEncoding() {
        RequestSegments requestSegments = new RequestSegments();
        requestSegments.add("/api/{id}");
        requestSegments.addIndex(0, "id");
        assertThat(requestSegments.resolve(new Object[]{12}))
                .isEqualTo("/api/12");
    }

    @Test
    public void segmentEncoding_withMultipleSegments() {
        RequestSegments requestSegments = new RequestSegments();
        requestSegments.add("/api/user/");
        requestSegments.add("{id}/");
        requestSegments.add("/contact/");
        requestSegments.addIndex(0, "id");
        assertThat(requestSegments.resolve(new Object[]{12}))
                .isEqualTo("/api/user/12/contact");
    }

    @Test
    public void segmentEncoding_withNullValue() {
        RequestSegments requestSegments = new RequestSegments();
        requestSegments.add("/api/{id}");
        requestSegments.addIndex(0, "id");
        assertThat(requestSegments.resolve(new Object[]{null}))
                .isEqualTo("/api/null");
    }

    @Test
    public void segmentEncoding_withMissingValue() {
        RequestSegments requestSegments = new RequestSegments();
        requestSegments.add("/api/{id}");
        assertThat(requestSegments.resolve(new Object[]{}))
                .isEqualTo("/api/{id}");
    }

    @Test
    public void segmentEncoding_withDuplicateSlash() {
        RequestSegments requestSegments = new RequestSegments();
        requestSegments.add("//api");
        assertThat(requestSegments.resolve(new Object[]{12}))
                .isEqualTo("/api");
    }

    @Test
    public void segmentEncoding_withSlashAtTheEnd() {
        RequestSegments requestSegments = new RequestSegments();
        requestSegments.add("/api/{id}/");
        requestSegments.addIndex(0, "id");
        assertThat(requestSegments.resolve(new Object[]{12}))
                .isEqualTo("/api/12");
    }

    @Test
    public void segmentEncoding_withEmptySegment() {
        RequestSegments requestSegments = new RequestSegments();
        requestSegments.add("api/  /{id}");
        requestSegments.addIndex(0, "id");
        assertThat(requestSegments.resolve(new Object[]{13}))
                .isEqualTo("/api/13");
    }
}