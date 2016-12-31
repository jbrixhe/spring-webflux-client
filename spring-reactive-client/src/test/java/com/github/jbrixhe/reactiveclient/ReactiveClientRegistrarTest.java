package com.github.jbrixhe.reactiveclient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ReactiveClientRegistrarTest {

    @Test
    public void getPackagesToScan_withValue() {
        assertThat(getPackage(ConfigurationWithValue.class))
                .containsExactlyInAnyOrder("packageValue1", "packageValue2");
    }

    @Test
    public void getPackagesToScan_withBasePackages() {
        assertThat(getPackage(ConfigurationWithBasePackages.class))
                .containsExactly("basePackage1", "basePackage2");
    }

    @Test
    public void getPackagesToScan_withBoth() {
        assertThat(getPackage(ConfigurationWithBoth.class))
                .containsExactly("packageValue1", "packageValue2","basePackage1", "basePackage2");
    }

    @Test
    public void getPackagesToScan_withNone() {
        assertThat(getPackage(ConfigurationWithNone.class))
                .containsExactly("com.github.jbrixhe.reactiveclient");
    }
    @Test
    public void getPackagesToScan_withSpaceAndEmpty() {
        assertThat(getPackage(ConfigurationWithSpaceAndEmpty.class))
                .containsExactly("packageValue2");
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

    @EnableReactiveClient(basePackages = {"basePackage1", "basePackage2"}, value = {"packageValue1", "packageValue2"})
    class ConfigurationWithBoth {
    }

    @EnableReactiveClient(basePackages = {"   "}, value = {"", "packageValue2"})
    class ConfigurationWithSpaceAndEmpty {
    }

    @EnableReactiveClient
    class ConfigurationWithNone {
    }
}