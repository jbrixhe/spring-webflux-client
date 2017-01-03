/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jbrixhe.reactiveclient;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.MalformedURLException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ReactiveClientBeanRegisterTests.Application.class, properties = {"reactive.url=http://property-url","reactive.url2=property-url2"})
@DirtiesContext
public class ReactiveClientBeanRegisterTests {

    @Autowired
    private ResourceLoader resourceLoader;

    @Configuration
    @EnableAutoConfiguration
    protected static class Application {

        public static void main(String[] args) {
            new SpringApplicationBuilder(Application.class).run(args);
        }
    }

    @Test
    public void getUrl() {
        ReactiveClientBeanRegister register = new ReactiveClientBeanRegister(null, resourceLoader);
        assertThat(register.getUrl(Collections.singletonMap("url","http://localhost")))
                .isEqualTo("http://localhost");
    }

    @Test
    public void getUrl_withoutProtocol() {
        ReactiveClientBeanRegister register = new ReactiveClientBeanRegister(null, resourceLoader);
        assertThat(register.getUrl(Collections.singletonMap("url","localhost")))
                .isEqualTo("http://localhost");
    }

    @Test
    public void getUrl_withPropertyAndProtocol() {
        ReactiveClientBeanRegister register = new ReactiveClientBeanRegister(null, resourceLoader);
        assertThat(register.getUrl(Collections.singletonMap("url","${reactive.url}")))
                .isEqualTo("http://property-url");
    }

    @Test
    public void getUrl_withProperty() {
        ReactiveClientBeanRegister register = new ReactiveClientBeanRegister(null, resourceLoader);
        assertThat(register.getUrl(Collections.singletonMap("url","${reactive.url2}")))
                .isEqualTo("http://property-url2");
    }

    @Test
    public void getUrl_withNoUrl() {
        ReactiveClientBeanRegister register = new ReactiveClientBeanRegister(null, resourceLoader);
        assertThat(register.getUrl(Collections.emptyMap()))
                .isNull();
    }

    @Test
    public void getUrl_withEmptyUrl() {
        ReactiveClientBeanRegister register = new ReactiveClientBeanRegister(null, resourceLoader);
        assertThat(register.getUrl(Collections.singletonMap("url","")))
                .isEmpty();
    }

    @Test
    public void getUrl_withOnlyWhitespace() {
        ReactiveClientBeanRegister register = new ReactiveClientBeanRegister(null, resourceLoader);
        assertThat(register.getUrl(Collections.singletonMap("url","    ")))
                .isEqualTo("    ");
    }

    @Test
    public void getUrl_withOtherProtocol() {
        ReactiveClientBeanRegister register = new ReactiveClientBeanRegister(null, resourceLoader);
        assertThat(register.getUrl(Collections.singletonMap("url","ftp://localhost")))
                .isEqualTo("ftp://localhost");
    }

    @Test
    public void getUrl_withMalformedURL() {
        ReactiveClientBeanRegister register = new ReactiveClientBeanRegister(null, resourceLoader);
        Assertions.assertThatThrownBy(()->register.getUrl(Collections.singletonMap("url","://localhost")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasCauseInstanceOf(MalformedURLException.class);
    }

    @Test
    public void getAliases() {
        ReactiveClientBeanRegister register = new ReactiveClientBeanRegister(null, resourceLoader);
        assertThat(register.getAliases(Collections.singletonMap("qualifier","beanQualifier")))
                .containsExactly("beanQualifier");
    }

    @Test
    public void getAliases_withEmptyValue() {
        ReactiveClientBeanRegister register = new ReactiveClientBeanRegister(null, resourceLoader);
        assertThat(register.getAliases(Collections.singletonMap("qualifier","")))
                .isEmpty();
    }

    @Test
    public void getAliases_withOnlyWhitespace() {
        ReactiveClientBeanRegister register = new ReactiveClientBeanRegister(null, resourceLoader);
        assertThat(register.getAliases(Collections.singletonMap("qualifier","   ")))
                .isEmpty();
    }

    @Test
    public void getPath() {
        ReactiveClientBeanRegister register = new ReactiveClientBeanRegister(null, resourceLoader);
        assertThat(register.getPath(Collections.singletonMap("path","/api")))
                .isEqualTo("/api");
    }

    @Test
    public void getPath_withDashAtTheEnd() {
        ReactiveClientBeanRegister register = new ReactiveClientBeanRegister(null, resourceLoader);
        assertThat(register.getPath(Collections.singletonMap("path","/api/")))
                .isEqualTo("/api");
    }

    @Test
    public void getPath_withoutDashAtTheBeginning() {
        ReactiveClientBeanRegister register = new ReactiveClientBeanRegister(null, resourceLoader);
        assertThat(register.getPath(Collections.singletonMap("path","api")))
                .isEqualTo("/api");
    }
}
