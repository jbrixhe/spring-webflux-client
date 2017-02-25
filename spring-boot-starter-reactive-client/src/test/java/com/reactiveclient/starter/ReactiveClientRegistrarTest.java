package com.reactiveclient.starter;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;

import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class ReactiveClientRegistrarTest {

    @Test
    public void getPackagesToScan_withValue() {
        Assertions.assertThat(getPackage(ConfigurationWithValue.class))
                .containsExactlyInAnyOrder("packageValue1", "packageValue2");
    }

    @Test
    public void getPackagesToScan_withBasePackages() {
        Assertions.assertThat(getPackage(ConfigurationWithBasePackages.class))
                .containsExactlyInAnyOrder("basePackage1", "basePackage2");
    }

    @Test
    public void getPackagesToScan_withNone() {
        Assertions.assertThat(getPackage(ConfigurationWithNone.class))
                .containsExactly("com.reactiveclient.starter");
    }

    @Test
    public void getPackagesToScan_withSpaceAndEmpty() {
        Assertions.assertThat(getPackage(ConfigurationWithSpaceAndEmpty.class))
                .containsExactly("packageValue1");
    }

    private Set<String> getPackage(Class<?> clazz) {
        ReactiveClientRegistrar clientRegistrar = new ReactiveClientRegistrar();
        AnnotationMetadata annotationMetadata = new StandardAnnotationMetadata(clazz);
        return clientRegistrar.getPackagesToScan(annotationMetadata);
    }

    @EnableReactiveClient({"packageValue1", "packageValue2"})
    static class ConfigurationWithValue {
    }

    @EnableReactiveClient(basePackages = {"basePackage1", "basePackage2"})
    static class ConfigurationWithBasePackages {
    }

    @EnableReactiveClient({"", "    ", "packageValue1"})
    class ConfigurationWithSpaceAndEmpty {
    }

    @EnableReactiveClient
    class ConfigurationWithNone {
    }
}