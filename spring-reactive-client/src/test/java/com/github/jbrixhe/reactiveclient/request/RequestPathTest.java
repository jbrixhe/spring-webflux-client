package com.github.jbrixhe.reactiveclient.request;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class RequestPathTest {

    @Test
    public void processPathSegment() {
        RequestPath requestPath = new RequestPath();
        requestPath.append("/api/{id}");
        assertThat(requestPath.getSegments())
                .hasSize(2);
    }

    @Test
    public void processPathSegment_withDuplicateSlash() {
        RequestPath requestPath = new RequestPath();
        requestPath.append("//{id}");
        assertThat(requestPath.getSegments())
                .hasSize(1);
    }

    @Test
    public void processPathSegment_withSlashAtTheEnd() {
        RequestPath requestPath = new RequestPath();
        requestPath.append("/api/{id}/");
        assertThat(requestPath.getSegments())
                .hasSize(2);
    }

    @Test
    public void processPathSegment_withEmptySegment() {
        RequestPath requestPath = new RequestPath();
        requestPath.append("/  /{id}");
        assertThat(requestPath.getSegments())
                .hasSize(1);
    }
}