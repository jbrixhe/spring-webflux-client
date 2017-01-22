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

package com.reactiveclient.beans;

import com.reactiveclient.EnableReactiveClient;
import com.reactiveclient.ReactiveClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ReactiveClientTests.Application.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {"reactive.url=http://host"})
@DirtiesContext
public class ReactiveClientTests {

    @Autowired
    private TestClient testClient;

    @Autowired
    private TestClient2 testClient2;

    @Qualifier("myAwesomeClient")
    @Autowired
    private TestClient2 testClient2WithQualifier;

    @Configuration
    @EnableAutoConfiguration
    @EnableReactiveClient
    protected static class Application {

        public static void main(String[] args) {
            new SpringApplicationBuilder(Application.class).run(args);
        }
    }

    @Test
    public void testClient() {
        assertNotNull("testClient was null", this.testClient);
        assertNotNull("testClient was null", this.testClient2);
        assertNotNull("testClient was null", this.testClient2WithQualifier);
    }

    @Configuration
    public static class TestDefaultFeignConfig {
    }

    @ReactiveClient(name = "localapp", url = "http://localhost:8080")
    private interface TestClient {
    }

    @ReactiveClient(name = "localapp2", qualifier = "myAwesomeClient", url = "http://localhost:8080")
    private interface TestClient2 {
    }
}
