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

package com.github.jbrixhe.reactiveclient.beans;

import com.github.jbrixhe.reactiveclient.EnableReactiveClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ReactiveClientTests.Application.class)
@DirtiesContext
public class ReactiveClientTests {

    @Autowired
    private TestClient testClient;

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
    }

    @Configuration
    public static class TestDefaultFeignConfig {
    }
}
