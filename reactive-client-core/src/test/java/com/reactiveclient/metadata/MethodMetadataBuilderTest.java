package com.reactiveclient.metadata;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URI;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MethodMetadataBuilderTest {
    @Test
    public void addPath() {
        MethodMetadata requestTemplate = MethodMetadata.newBuilder(URI.create("http://localhost:8080"))
                .addPath("/api/{id}")
                .build();

        assertThat(requestTemplate.getRequestTemplate().getUriBuilder().build(Collections.singletonMap("id", 132)))
                .isEqualTo(URI.create("http://localhost:8080/api/132"));
    }

    @Test
    public void addPath_withMultipleSegments() {
        MethodMetadata requestTemplate = MethodMetadata.newBuilder(URI.create("http://localhost:8080"))
                .addPath("/api/users/")
                .addPath("{id}/")
                .addPath("/contact")
                .build();

        assertThat(requestTemplate.getRequestTemplate().getUriBuilder().build(Collections.singletonMap("id", 123)))
                .isEqualTo(URI.create("http://localhost:8080/api/users/123/contact"));
    }

    @Test
    public void addQueryParam() {
        MethodMetadata requestTemplate = MethodMetadata.newBuilder(URI.create("http://localhost:8080"))
                .addPath("/api/users")
                .addParameter(1, "name")
                .build();

        assertThat(requestTemplate.getRequestTemplate().getUriBuilder().build(Collections.singletonMap("name", "Jérémy")))
                .isEqualTo(URI.create("http://localhost:8080/api/users?name=J%C3%A9r%C3%A9my"));
    }
}