package com.github.jbrixhe.reactiveclient.request.parameter;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestParametersTest {

    @Test
    public void testRequestParameter() {
        RequestParameters requestParameters = new RequestParameters();
        requestParameters.add("stringParameter", String.class);
        requestParameters.add("integerParameter", Integer.class);
        requestParameters.add("doubleParameter", Double.class);
        requestParameters.add("arrayParameter", Object[].class);
        requestParameters.add("collectionParameter", List.class);

        Map<String,Object> parameterValues = new HashMap<String,Object>(){{
            put("stringParameter","StringValue");
            put("integerParameter",13);
            put("doubleParameter",23.09D);
            put("arrayParameter",new Integer[]{1,2});
            put("collectionParameter",asList("stringvalue1","stringvalue2"));
        }};

        Assertions.assertThat(requestParameters.resolve(parameterValues))
                .isEqualTo("?doubleParameter=23.09&collectionParameter=stringvalue1&collectionParameter=stringvalue2&arrayParameter=1&arrayParameter=2&integerParameter=13&stringParameter=StringValue");
    }

    @Test
    public void testRequestParameter_withCollection() {
        RequestParameters requestParameters = new RequestParameters();
        requestParameters.add("collectionParameter", ArrayList.class);

        Assertions.assertThat(requestParameters.resolve(singletonMap("collectionParameter", asList(1,2,3))))
                .isEqualTo("?collectionParameter=1&collectionParameter=2&collectionParameter=3");
    }

    @Test
    public void testRequestParameter_withArray() {
        RequestParameters requestParameters = new RequestParameters();
        requestParameters.add("arrayParameter", Integer[].class);

        Assertions.assertThat(requestParameters.resolve(singletonMap("arrayParameter", new Integer[]{1,2,3})))
                .isEqualTo("?arrayParameter=1&arrayParameter=2&arrayParameter=3");
    }

    @Test
    public void testRequestParameter_withSimpleType() {
        RequestParameters requestParameters = new RequestParameters();
        requestParameters.add("stringParameter", String.class);
        requestParameters.add("integerParameter", Integer.class);
        requestParameters.add("doubleParameter", Double.class);

        Map<String,Object> parameterValues = new HashMap<String,Object>(){{
            put("stringParameter","StringValue");
            put("integerParameter",13);
            put("doubleParameter",23.09D);
        }};

        Assertions.assertThat(requestParameters.resolve(parameterValues))
                .isEqualTo("?doubleParameter=23.09&integerParameter=13&stringParameter=StringValue");
    }

    @Test
    public void testRequestParameter_withDuplicateParameterName() {
        RequestParameters requestParameters = new RequestParameters();
        requestParameters.add("duplicateName", Integer[].class);

        assertThatThrownBy(()->requestParameters.add("duplicateName", Integer[].class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageEndingWith("duplicateName");
    }
}