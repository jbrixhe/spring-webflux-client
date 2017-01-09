package com.github.jbrixhe.reactiveclient.request;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(MockitoJUnitRunner.class)
public class RequestTemplateAnnotationVisitorTest {
    @InjectMocks
    private RequestTemplateAnnotationVisitor requestTemplateAnnotationVisitor;

    @Test
    public void processRootRequestTemplate_withSingleInterface() {
        RequestTemplate requestTemplate = requestTemplateAnnotationVisitor.processRootRequestTemplate(ParentReactiveClient.class);
        assertThat(requestTemplate).isNotNull();
        assertThat(requestTemplate.getRequestPath().getSegments()).hasSize(1);
    }

    @Test
    public void processRootRequestTemplate_withOneParentInterface() {
        RequestTemplate requestTemplate = requestTemplateAnnotationVisitor.processRootRequestTemplate(ChildReactiveClient.class);
        assertThat(requestTemplate).isNotNull();
        assertThat(requestTemplate.getRequestPath().getSegments()).hasSize(2);
    }

    @Test
    public void processRootRequestTemplate_withNoRequestMappingOnClass() {
        RequestTemplate requestTemplate = requestTemplateAnnotationVisitor.processRootRequestTemplate(SimpleInterface.class);
        assertThat(requestTemplate).isNotNull();
        assertThat(requestTemplate.getRequestPath().getSegments()).isEmpty();
    }

    @Test
    public void processRootRequestTemplate_withTooManyParent() {
        assertThatThrownBy(() -> requestTemplateAnnotationVisitor.processRootRequestTemplate(ChildReactiveClientWithTwoDirectParents.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void visit() {
        assertThat(requestTemplateAnnotationVisitor.visit(ChildReactiveClient.class))
                .hasSize(2)
                .extracting("requestPath.segments.size")
                .containsExactlyInAnyOrder(2,3);
    }

    @Test
    public void parsePath() {
        RequestTemplate requestTemplate = new RequestTemplate();
        requestTemplateAnnotationVisitor.parsePath(Collections.singletonMap("value", new String[]{"/api"}), requestTemplate);
        assertThat(requestTemplate.getRequestPath().getSegments()).hasSize(1);
    }

    @Test
    public void parsePath_withNoValue() {
        RequestTemplate requestTemplate = new RequestTemplate();
        requestTemplateAnnotationVisitor.parsePath(Collections.singletonMap("value", new String[]{}), requestTemplate);
        assertThat(requestTemplate.getRequestPath().getSegments()).isEmpty();
    }

    @Test
    public void parsePath_withTooValue() {
        RequestTemplate requestTemplate = new RequestTemplate();
        assertThatThrownBy(()-> requestTemplateAnnotationVisitor.parsePath(Collections.singletonMap("value", new String[]{"/parent", "/child"}), requestTemplate))
            .isInstanceOf(IllegalArgumentException.class);
    }

    interface SimpleInterface {}

    @RequestMapping("/parent")
    interface ParentReactiveClient {}

    @RequestMapping("/parentbis")
    interface ParentReactiveClientBis {}

    @RequestMapping("/child")
    interface ChildReactiveClient extends ParentReactiveClient {

        void testGet();

        @RequestMapping("/get2")
        void testGet2();

    }

    @RequestMapping("/grandChild")
    interface ChildReactiveClientWithTwoDirectParents extends ChildReactiveClient, ParentReactiveClient {}
}