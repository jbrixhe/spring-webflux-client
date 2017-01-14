package com.github.jbrixhe.reactiveclient.request.parameter;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.Arrays;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(MockitoJUnitRunner.class)
public class RequestParametersTest {

    @Test
    public void testRequestParameter() {
        new DefaultConversionService().convert(Arrays.asList(1, 2, 3, 4), String.class);
        RequestParameters requestParameters = new RequestParameters();
        addParameter(requestParameters, "stringParameter", 0);
        addParameter(requestParameters, "integerParameter", 1);
        addParameter(requestParameters, "doubleParameter", 2);
        addParameter(requestParameters, "arrayParameter", 3);
        addParameter(requestParameters, "collectionParameter", 4);

        Assertions.assertThat(requestParameters.resolve(new Object[]{"StringValue", 13, 23.09D, new Integer[]{1, 2}, asList("stringValue1", "stringValue2")}))
                .isEqualTo("?doubleParameter=23.09&collectionParameter=stringValue1&collectionParameter=stringValue2&arrayParameter=1&arrayParameter=2&integerParameter=13&stringParameter=StringValue");
    }

    @Test
    public void testRequestParameter_withCollection() {
        RequestParameters requestParameters = addParameter(new RequestParameters(), "collectionParameter", 0);

        Assertions.assertThat(requestParameters.resolve(new Object[]{asList(1, 2, 3)}))
                .isEqualTo("?collectionParameter=1&collectionParameter=2&collectionParameter=3");
    }

    @Test
    public void testRequestParameter_withArray() {
        RequestParameters requestParameters = addParameter(new RequestParameters(), "arrayParameter", 0);

        Assertions.assertThat(requestParameters.resolve(new Object[]{new Integer[]{1, 2, 3}}))
                .isEqualTo("?arrayParameter=1&arrayParameter=2&arrayParameter=3");
    }

    @Test
    public void testRequestParameter_withSimpleType() {
        RequestParameters requestParameters = new RequestParameters();
        addParameter(requestParameters, "stringParameter", 0);
        addParameter(requestParameters, "integerParameter", 1);
        addParameter(requestParameters, "doubleParameter", 2);

        Assertions.assertThat(requestParameters.resolve(new Object[]{"StringValue", 13, 23.09D}))
                .isEqualTo("?doubleParameter=23.09&integerParameter=13&stringParameter=StringValue");
    }

    private RequestParameters addParameter(RequestParameters requestParameters, String parameterName, Integer parameterIndex) {
        requestParameters.add(parameterName);
        requestParameters.addIndex(parameterIndex, parameterName);
        return requestParameters;
    }
}