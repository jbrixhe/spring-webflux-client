package com.github.jbrixhe.reactiveclient.metadata.request;

import com.github.jbrixhe.reactiveclient.metadata.request.RequestHeader.DynamicRequestHeader;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

@RunWith(MockitoJUnitRunner.class)
public class RequestHeadersTest {

    @Test
    public void requestHeaders_withDynamicHeader() {
        RequestHeaders requestHeaders = getDynamic("header1", 0);

        HttpHeaders httpHeaders = requestHeaders.encode(new Object[]{"headerDynamicHeader"});
        Assertions.assertThat(httpHeaders)
                .containsExactly(new SimpleEntry<>("header1", singletonList("headerDynamicHeader")));
    }

    @Test
    public void requestHeaders_withStaticHeader() {
        RequestHeaders requestHeaders = getBasic("header1", "headerBasicValue");

        HttpHeaders httpHeaders = requestHeaders.encode(new Object[]{});
        Assertions.assertThat(httpHeaders)
                .containsExactly(new SimpleEntry<>("header1", singletonList("headerBasicValue")));
    }

    @Test
    public void requestHeaders_withStaticAndDynamicHeaders() {
        Map<String, RequestHeader> requestHeaderByName = new HashMap<>();
        requestHeaderByName.put("header1", new RequestHeader.DynamicRequestHeader("header1"));
        requestHeaderByName.put("header2", new RequestHeader.BasicRequestHeader("header2", "headerBasicValue"));

        RequestHeaders requestHeaders = new RequestHeaders(requestHeaderByName, singletonMap(0, "header1"));

        HttpHeaders httpHeaders = requestHeaders.encode(new Object[]{"headerDynamicHeader"});
        Assertions.assertThat(httpHeaders)
                .hasSize(2)
                .contains(new SimpleEntry<>("header1", singletonList("headerDynamicHeader")),
                        new SimpleEntry<>("header2", singletonList("headerBasicValue")));
    }

    @Test
    public void requestHeaders_withArray() {
        RequestHeaders requestHeaders = getDynamic("header1", 0);

        HttpHeaders httpHeaders = requestHeaders.encode(new Object[]{new Integer[]{1, 2, 3}});
        Assertions.assertThat(httpHeaders)
                .containsExactly(new SimpleEntry<>("header1", asList("1", "2", "3")));
    }

    @Test
    public void requestHeaders_withList() {
        RequestHeaders requestHeaders = getDynamic("header1", 0);

        HttpHeaders httpHeaders = requestHeaders.encode(new Object[]{asList(12.23D, 234.321D)});
        Assertions.assertThat(httpHeaders)
                .containsExactly(new SimpleEntry<>("header1", asList("12.23", "234.321")));
    }

    @Test
    public void requestHeaders_withEmptyArray() {
        RequestHeaders requestHeaders = getDynamic("header1", 0);

        HttpHeaders httpHeaders = requestHeaders.encode(new Object[]{new Integer[]{}});
        Assertions.assertThat(httpHeaders)
                .isEmpty();
    }

    @Test
    public void requestHeaders_withEmptyList() {
        RequestHeaders requestHeaders = getDynamic("header1", 0);

        HttpHeaders httpHeaders = requestHeaders.encode(new Object[]{emptyList()});
        Assertions.assertThat(httpHeaders)
                .isEmpty();
    }

    private RequestHeaders getDynamic(String name, Integer index) {
        return new RequestHeaders(singletonMap(name, new DynamicRequestHeader(name)), singletonMap(index, name));
    }

    private RequestHeaders getBasic(String name, String value) {
        return new RequestHeaders(singletonMap(name, new RequestHeader.BasicRequestHeader(name, value)), emptyMap());
    }
}