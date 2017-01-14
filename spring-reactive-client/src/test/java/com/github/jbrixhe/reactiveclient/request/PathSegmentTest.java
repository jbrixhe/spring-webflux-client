package com.github.jbrixhe.reactiveclient.request;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PathSegmentTest {

    @Test
    public void fromString_withStaticSegment() {
        assertThat(PathSegment.fromString("api"))
                .isInstanceOf(PathSegment.StaticPathSegment.class);
    }

    @Test
    public void fromString_withDynamicSegment() {
        assertThat(PathSegment.fromString("{pathSegment}"))
                .isInstanceOf(PathSegment.DynamicPathSegment.class);
    }

}