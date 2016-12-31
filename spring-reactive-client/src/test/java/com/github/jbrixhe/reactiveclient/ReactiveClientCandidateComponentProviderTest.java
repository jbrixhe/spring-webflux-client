package com.github.jbrixhe.reactiveclient;

import com.github.jbrixhe.reactiveclient.toscan.TestInterfaceReactiveClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ReactiveClientCandidateComponentProviderTest.PlainConfiguration.class)
@DirtiesContext
public class ReactiveClientCandidateComponentProviderTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    public void findCandidateComponents() {
        ReactiveClientCandidateComponentProvider componentProvider = new ReactiveClientCandidateComponentProvider(resourceLoader);
        Set<BeanDefinition> candidateComponents = componentProvider.findCandidateComponents("com.github.jbrixhe.reactiveclient.toscan");
        assertEquals("", candidateComponents.size(), 1);
        assertEquals("", candidateComponents.iterator().next().getBeanClassName(), TestInterfaceReactiveClient.class.getName());
    }

    @Configuration
    @EnableAutoConfiguration
    @RestController
    protected static class PlainConfiguration {
    }
}