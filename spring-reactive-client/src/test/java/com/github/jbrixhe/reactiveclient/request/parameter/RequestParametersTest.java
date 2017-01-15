package com.github.jbrixhe.reactiveclient.request.parameter;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;

@RunWith(MockitoJUnitRunner.class)
public class RequestParametersTest {

    @Test
    public void testRequestParameter() {
        Map<Integer, String> indexToName = new HashMap<>();
        indexToName.put(0, "stringParameter");
        indexToName.put(1, "integerParameter");
        indexToName.put(2, "doubleParameter");
        indexToName.put(3, "arrayParameter");
        indexToName.put(4, "collectionParameter");
        Map<String, RequestParameter> nameToParameter = new HashMap<>();
        nameToParameter.put("stringParameter", new RequestParameter.DynamicRequestParameter("stringParameter"));
        nameToParameter.put("integerParameter", new RequestParameter.DynamicRequestParameter("integerParameter"));
        nameToParameter.put("doubleParameter", new RequestParameter.DynamicRequestParameter("doubleParameter"));
        nameToParameter.put("arrayParameter", new RequestParameter.DynamicRequestParameter("arrayParameter"));
        nameToParameter.put("collectionParameter", new RequestParameter.DynamicRequestParameter("collectionParameter"));

        RequestParameters requestParameters = new RequestParameters(nameToParameter, indexToName);
        Assertions.assertThat(requestParameters.resolve(new Object[]{"StringValue", 13, 23.09D, new Integer[]{1, 2}, asList("stringValue1", "stringValue2")}))
                .isEqualTo("?doubleParameter=23.09&collectionParameter=stringValue1&collectionParameter=stringValue2&arrayParameter=1&arrayParameter=2&integerParameter=13&stringParameter=StringValue");
    }

    @Test
    public void testRequestParameter_withCollection() {
        Map<Integer, String> indexToName = new HashMap<>();
        indexToName.put(0, "collectionParameter");

        Map<String, RequestParameter> nameToParameter = new HashMap<>();
        nameToParameter.put("collectionParameter", new RequestParameter.DynamicRequestParameter("collectionParameter"));

        RequestParameters requestParameters = new RequestParameters(nameToParameter, indexToName);
        Assertions.assertThat(requestParameters.resolve(new Object[]{asList(1, 2, 3)}))
                .isEqualTo("?collectionParameter=1&collectionParameter=2&collectionParameter=3");
    }

    @Test
    public void testRequestParameter_withArray() {
        Map<Integer, String> indexToName = new HashMap<>();
        indexToName.put(0, "arrayParameter");

        Map<String, RequestParameter> nameToParameter = new HashMap<>();
        nameToParameter.put("arrayParameter", new RequestParameter.DynamicRequestParameter("arrayParameter"));

        RequestParameters requestParameters = new RequestParameters(nameToParameter, indexToName);

        Assertions.assertThat(requestParameters.resolve(new Object[]{new Integer[]{1, 2, 3}}))
                .isEqualTo("?arrayParameter=1&arrayParameter=2&arrayParameter=3");
    }

    @Test
    public void testRequestParameter_withSimpleType() {
        Map<Integer, String> indexToName = new HashMap<>();
        indexToName.put(0, "stringParameter");
        indexToName.put(1, "integerParameter");
        indexToName.put(2, "doubleParameter");
        Map<String, RequestParameter> nameToParameter = new HashMap<>();
        nameToParameter.put("stringParameter", new RequestParameter.DynamicRequestParameter("stringParameter"));
        nameToParameter.put("integerParameter", new RequestParameter.DynamicRequestParameter("integerParameter"));
        nameToParameter.put("doubleParameter", new RequestParameter.DynamicRequestParameter("doubleParameter"));
        nameToParameter.put("arrayParameter", new RequestParameter.DynamicRequestParameter("arrayParameter"));
        nameToParameter.put("collectionParameter", new RequestParameter.DynamicRequestParameter("collectionParameter"));

        RequestParameters requestParameters = new RequestParameters(nameToParameter, indexToName);
        Assertions.assertThat(requestParameters.resolve(new Object[]{"StringValue", 13, 23.09D}))
                .isEqualTo("?doubleParameter=23.09&integerParameter=13&stringParameter=StringValue");
    }
}