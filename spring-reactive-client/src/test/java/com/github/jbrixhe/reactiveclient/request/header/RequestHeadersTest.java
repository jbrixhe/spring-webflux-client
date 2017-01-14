package com.github.jbrixhe.reactiveclient.request.header;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;

import java.util.AbstractMap.SimpleEntry;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@RunWith(MockitoJUnitRunner.class)
public class RequestHeadersTest {

    @Test
    public void requestHeaders_withDynamicHeader() {
        RequestHeaders requestHeaders = new RequestHeaders();
        addHeader(requestHeaders, "header1", 0);

        HttpHeaders httpHeaders = requestHeaders.encode(new Object[]{"headerDynamicHeader"});
        Assertions.assertThat(httpHeaders)
                .containsExactly(new SimpleEntry<>("header1", singletonList("headerDynamicHeader")));
    }

    @Test
    public void requestHeaders_withStaticHeader() {
        RequestHeaders requestHeaders = new RequestHeaders();
        requestHeaders.add("header1", "headerStaticValue");

        HttpHeaders httpHeaders = requestHeaders.encode(new Object[]{});
        Assertions.assertThat(httpHeaders)
                .containsExactly(new SimpleEntry<>("header1", singletonList("headerStaticValue")));
    }

    @Test
    public void requestHeaders_withStaticAndDynamicHeaders() {
        RequestHeaders requestHeaders = new RequestHeaders();
        addHeader(requestHeaders, "header1", 0);
        requestHeaders.add("header2", "headerStaticValue");

        HttpHeaders httpHeaders = requestHeaders.encode(new Object[]{"headerDynamicHeader"});
        Assertions.assertThat(httpHeaders)
                .containsExactly(new SimpleEntry<>("header1", singletonList("headerDynamicHeader")),
                        new SimpleEntry<>("header2", singletonList("headerStaticValue")));
    }

    @Test
    public void requestHeaders_withArray() {
        RequestHeaders requestHeaders = new RequestHeaders();
        addHeader(requestHeaders, "header1", 0);

        HttpHeaders httpHeaders = requestHeaders.encode(new Object[]{new Integer[]{1, 2, 3}});
        Assertions.assertThat(httpHeaders)
                .containsExactly(new SimpleEntry<>("header1", asList("1", "2", "3")));
    }

    @Test
    public void requestHeaders_withList() {
        RequestHeaders requestHeaders = new RequestHeaders();
        addHeader(requestHeaders, "header1", 0);

        HttpHeaders httpHeaders = requestHeaders.encode(new Object[]{asList(12.23D, 234.321D)});
        Assertions.assertThat(httpHeaders)
                .containsExactly(new SimpleEntry<>("header1", asList("12.23", "234.321")));
    }

    @Test
    public void requestHeaders_withEmptyArray() {
        RequestHeaders requestHeaders = new RequestHeaders();
        addHeader(requestHeaders, "header1", 0);

        HttpHeaders httpHeaders = requestHeaders.encode(new Object[]{new Integer[]{}});
        Assertions.assertThat(httpHeaders)
                .isEmpty();
    }

    @Test
    public void requestHeaders_withEmptyList() {
        RequestHeaders requestHeaders = new RequestHeaders();
        addHeader(requestHeaders, "header1", 0);

        HttpHeaders httpHeaders = requestHeaders.encode(new Object[]{emptyList()});
        Assertions.assertThat(httpHeaders)
                .isEmpty();
    }

    private void addHeader(RequestHeaders requestHeaders, String name, Integer index) {
        requestHeaders.add(name);
        requestHeaders.addIndex(index, name);
    }
}