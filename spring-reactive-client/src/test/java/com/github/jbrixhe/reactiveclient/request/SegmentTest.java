package com.github.jbrixhe.reactiveclient.request;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SegmentTest {

    @Test
    public void fromString_withStaticSegment() {
        assertThat(Segment.fromString("api"))
                .isInstanceOf(Segment.StaticSegment.class);
    }

    @Test
    public void fromString_withDynamicSegment() {
        assertThat(Segment.fromString("{pathSegment}"))
                .isInstanceOf(Segment.DynamicSegment.class);
    }

}