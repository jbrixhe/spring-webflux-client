package com.webfluxclient.handler;

import org.junit.Test;
import org.springframework.core.ResolvableType;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultReactiveMethodHandlerTest {

    @Test
    public void isFormData() {
        Method formDataMethod = ReflectionUtils.findMethod(getClass(), "multiValueMap");
        ResolvableType resolvableType = ResolvableType.forMethodReturnType(formDataMethod);
        assertThat(new DefaultReactiveMethodHandler().isFormData(resolvableType))
                .isTrue();
    }

    @Test
    public void isFormData_withWrongGeneric() {
        Method formDataMethod = ReflectionUtils.findMethod(getClass(), "wrongMultiValueMap");
        ResolvableType resolvableType = ResolvableType.forMethodReturnType(formDataMethod);
        assertThat(new DefaultReactiveMethodHandler().isFormData(resolvableType))
                .isFalse();
    }

    MultiValueMap<String, Integer> wrongMultiValueMap() {
        return null;
    }

    MultiValueMap<String, String> multiValueMap() {
        return null;
    }

}