package com.github.jbrixhe.reactiveclient.request;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.AbstractMap.SimpleEntry;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class RequestTemplateBuilderTest {
    @Test
    public void addPath() {
        RequestTemplate requestTemplate = RequestTemplate.newBuilder()
                .addPath("/api/{id}")
                .build();

        assertThat(requestTemplate.getRequestSegments().getRequestSegments())
                .extracting("segment")
                .containsExactly("api", "id");
    }

    @Test
    public void addPath_withMultipleSegments() {
        RequestTemplate requestTemplate = RequestTemplate.newBuilder()
                .addPath("/api/user/")
                .addPath("{id}/")
                .addPath("/contact/")
                .build();

        assertThat(requestTemplate.getRequestSegments().getRequestSegments())
                .extracting("segment")
                .containsExactly("api", "user", "id", "contact");
    }

    @Test
    public void addPath_withDuplicateSlash() {
        RequestTemplate requestTemplate = RequestTemplate.newBuilder()
                .addPath("/api//user")
                .build();

        assertThat(requestTemplate.getRequestSegments().getRequestSegments())
                .extracting("segment")
                .containsExactly("api", "user");
    }

    @Test
    public void addPath_withSlashAtTheEnd() {
        RequestTemplate requestTemplate = RequestTemplate.newBuilder()
                .addPath("/api/user/")
                .build();

        assertThat(requestTemplate.getRequestSegments().getRequestSegments())
                .extracting("segment")
                .containsExactly("api", "user");
    }

    @Test
    public void addPath_withEmptySegment() {
        RequestTemplate requestTemplate = RequestTemplate.newBuilder()
                .addPath("/api/   /user/")
                .build();

        assertThat(requestTemplate.getRequestSegments().getRequestSegments())
                .extracting("segment")
                .containsExactly("api", "user");
    }
}