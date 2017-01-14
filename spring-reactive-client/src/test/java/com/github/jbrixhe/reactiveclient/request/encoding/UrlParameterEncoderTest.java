package com.github.jbrixhe.reactiveclient.request.encoding;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class UrlParameterEncoderTest {
    @InjectMocks
    private UrlParameterEncoder urlParameterEncoder;

    @Test
    public void convertToString() {
        assertThat(urlParameterEncoder.convertToString("Awesome value"))
                .isEqualTo("Awesome+value");
    }

    @Test
    public void convertToString_withListOrArray() {
        assertThat(urlParameterEncoder.convertToString("1,2,3"))
                .isEqualTo("1%2C2%2C3");
    }
}