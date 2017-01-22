package com.reactiveclient.metadata;

import com.reactiveclient.metadata.MethodMetadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MethodMetadataBuilderTest {
    @Test
    public void addPath() {
        MethodMetadata requestTemplate = MethodMetadata.newBuilder()
                .addPath("/api/{id}")
                .build();

        assertThat(requestTemplate.getRequestTemplate().getRequestSegments().getRequestSegments())
                .extracting("segment")
                .containsExactly("api", "id");
    }

    @Test
    public void addPath_withMultipleSegments() {
        MethodMetadata requestTemplate = MethodMetadata.newBuilder()
                .addPath("/api/user/")
                .addPath("{id}/")
                .addPath("/contact/")
                .build();

        assertThat(requestTemplate.getRequestTemplate().getRequestSegments().getRequestSegments())
                .extracting("segment")
                .containsExactly("api", "user", "id", "contact");
    }

    @Test
    public void addPath_withDuplicateSlash() {
        MethodMetadata requestTemplate = MethodMetadata.newBuilder()
                .addPath("/api//user")
                .build();

        assertThat(requestTemplate.getRequestTemplate().getRequestSegments().getRequestSegments())
                .extracting("segment")
                .containsExactly("api", "user");
    }

    @Test
    public void addPath_withSlashAtTheEnd() {
        MethodMetadata requestTemplate = MethodMetadata.newBuilder()
                .addPath("/api/user/")
                .build();

        assertThat(requestTemplate.getRequestTemplate().getRequestSegments().getRequestSegments())
                .extracting("segment")
                .containsExactly("api", "user");
    }

    @Test
    public void addPath_withEmptySegment() {
        MethodMetadata requestTemplate = MethodMetadata.newBuilder()
                .addPath("/api/   /user/")
                .build();

        assertThat(requestTemplate.getRequestTemplate().getRequestSegments().getRequestSegments())
                .extracting("segment")
                .containsExactly("api", "user");
    }
}