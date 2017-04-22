package com.webfluxclient.metadata.request.encoding;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.AbstractMap.SimpleEntry;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DefaultParameterEncoderTest {

    @InjectMocks
    private DefaultParameterEncoder defaultParameterEncoder;

    @Test
    public void convertToString_withSimpleObject() {
        assertThat(defaultParameterEncoder.convertToString(singletonMap(0, "parameterName"), new Object[]{12.23D}))
                .containsExactly(new SimpleEntry<>("parameterName", "12.23"));
    }

    @Test
    public void convertToString_withList() {
        assertThat(defaultParameterEncoder.convertToString(singletonMap(0, "parameterName"), new Object[]{asList(1, 2, 3)}))
                .containsExactly(new SimpleEntry<>("parameterName", "1,2,3"));
    }

    @Test
    public void convertToString_withArray() {
        assertThat(defaultParameterEncoder.convertToString(singletonMap(0, "parameterName"), new Object[]{new Integer[]{1, 2, 3}}))
                .containsExactly(new SimpleEntry<>("parameterName", "1,2,3"));
    }

    @Test
    public void convertToString_withNull() {
        assertThat(defaultParameterEncoder.convertToString(singletonMap(0, "parameterName"), new Object[]{null}))
                .containsExactly(new SimpleEntry<String, String>("parameterName", null));
    }

    @Test
    public void convertToListOfString_withSimpleObject() {
        assertThat(defaultParameterEncoder.convertToListOfString(singletonMap(0, "parameterName"), new Object[]{12.23D}))
                .containsExactly(new SimpleEntry<>("parameterName", singletonList("12.23")));
    }

    @Test
    public void convertToListOfString_withList() {
        assertThat(defaultParameterEncoder.convertToListOfString(singletonMap(0, "parameterName"), new Object[]{asList(1, 2, 3)}))
                .containsExactly(new SimpleEntry<>("parameterName", asList("1", "2", "3")));
    }

    @Test
    public void convertToListOfString_withArray() {
        assertThat(defaultParameterEncoder.convertToListOfString(singletonMap(0, "parameterName"), new Object[]{new Integer[]{1, 2, 3}}))
                .containsExactly(new SimpleEntry<>("parameterName", asList("1", "2", "3")));
    }

    @Test
    public void convertToListOfString_withNull() {
        assertThat(defaultParameterEncoder.convertToListOfString(singletonMap(0, "parameterName"), new Object[]{null}))
                .containsExactly(new SimpleEntry<>("parameterName", emptyList()));
    }

    @Test
    public void processValue_withSimpleObject() {
        assertThat(defaultParameterEncoder.processValue(1))
                .containsExactly("1");
    }

    @Test
    public void processValue_withArray() {
        assertThat(defaultParameterEncoder.processValue(new Integer[]{2, 3, 4}))
                .containsExactly("2", "3", "4");
    }

    @Test
    public void processValue_withList() {
        assertThat(defaultParameterEncoder.processValue(asList(5, 6, 7)))
                .containsExactly("5", "6", "7");
    }

    @Test
    public void processValue_withNullInList() {
        assertThat(defaultParameterEncoder.processValue(asList(8, null, 9)))
                .containsExactly("8", "9");
    }

    @Test
    public void processValue_withNullInArray() {
        assertThat(defaultParameterEncoder.processValue(new Integer[]{10, 11, null}))
                .containsExactly("10", "11");
    }

    @Test
    public void processValue_withNull() {
        assertThat(defaultParameterEncoder.processValue(null))
                .isEmpty();
    }

}