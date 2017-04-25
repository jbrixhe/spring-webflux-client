package com.webfluxclient.utils;

import org.junit.Test;
import org.springframework.core.ResolvableType;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TypesTest {

    @Test
    public void isFormData() {
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, String.class);
        assertThat(Types.isFormData(resolvableType))
                .isTrue();
    }

    @Test
    public void isFormData_withWrongRootType() {
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(Map.class, String.class, String.class);
        assertThat(Types.isFormData(resolvableType))
                .isFalse();
    }

    @Test
    public void isFormData_withWrongGeneric() {
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, Integer.class);
        assertThat(Types.isFormData(resolvableType))
                .isFalse();
    }
}