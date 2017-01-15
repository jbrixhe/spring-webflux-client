package com.github.jbrixhe.reactiveclient.request;

import com.github.jbrixhe.reactiveclient.request.header.RequestHeader.BasicRequestHeader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(MockitoJUnitRunner.class)
public class RequestTemplateAnnotationVisitorTest {
    @InjectMocks
    private RequestTemplateAnnotationVisitor requestTemplateAnnotationVisitor;

    @Test
    public void processRootRequestTemplate_withSingleInterface() {
        RequestTemplate requestTemplate = requestTemplateAnnotationVisitor.processRootRequestTemplate(ParentReactiveClient.class);
        assertThat(requestTemplate.getRequestSegments().getRequestSegments())
                .extracting("segment")
                .containsExactly("parent");
    }

    @Test
    public void processRootRequestTemplate_withOneParentInterface() {
        RequestTemplate requestTemplate = requestTemplateAnnotationVisitor.processRootRequestTemplate(ChildReactiveClient.class);
        assertThat(requestTemplate.getRequestSegments().getRequestSegments())
                .extracting("segment")
                .containsExactly("parent", "child");
    }

    @Test
    public void processRootRequestTemplate_withNoRequestMappingOnClass() {
        RequestTemplate requestTemplate = requestTemplateAnnotationVisitor.processRootRequestTemplate(SimpleInterface.class);
        assertThat(requestTemplate.getRequestSegments().getRequestSegments())
                .isEmpty();
    }

    @Test
    public void processRootRequestTemplate_withTooManyParent() {
        assertThatThrownBy(() -> requestTemplateAnnotationVisitor.processRootRequestTemplate(ChildReactiveClientWithTwoDirectParents.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void parsePath() {
        RequestTemplate.Builder requestTemplateBuilder = RequestTemplate.newBuilder();
        requestTemplateAnnotationVisitor.parsePath(singletonMap("value", new String[]{"/api"}), requestTemplateBuilder);
        assertThat(requestTemplateBuilder.build().getRequestSegments().getRequestSegments())
                .hasSize(1)
                .extracting("segment")
                .containsExactly("api");
    }

    @Test
    public void parsePath_withNoValue() {
        RequestTemplate.Builder requestTemplateBuilder = RequestTemplate.newBuilder();
        requestTemplateAnnotationVisitor.parsePath(singletonMap("value", new String[]{}), requestTemplateBuilder);
        assertThat(requestTemplateBuilder.build().getRequestSegments().getRequestSegments())
                .isEmpty();
    }

    @Test
    public void parsePath_withTooManyValue() {
        assertThatThrownBy(() -> requestTemplateAnnotationVisitor.parsePath(singletonMap("value", new String[]{"/parent", "/child"}), RequestTemplate.newBuilder()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void parseMethod() {
        RequestTemplate.Builder requestTemplateBuilder = RequestTemplate.newBuilder();
        requestTemplateAnnotationVisitor.parseMethod(singletonMap("method", new RequestMethod[]{RequestMethod.GET}), requestTemplateBuilder);
        assertThat(requestTemplateBuilder.build().getHttpMethod()).isEqualTo(HttpMethod.GET);
    }

    @Test
    public void parseMethod_withNoMethod() {
        RequestTemplate.Builder requestTemplateBuilder = RequestTemplate.newBuilder();
        requestTemplateAnnotationVisitor.parseMethod(singletonMap("method", new RequestMethod[]{}), requestTemplateBuilder);
        assertThat(requestTemplateBuilder.build().getHttpMethod()).isEqualTo(HttpMethod.GET);
    }

    @Test
    public void parseMethod_withTooManyMethod() {
        assertThatThrownBy(() -> requestTemplateAnnotationVisitor.parseMethod(singletonMap("method", new RequestMethod[]{RequestMethod.PUT, RequestMethod.POST}), RequestTemplate.newBuilder()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void parseHeaders() {
        RequestTemplate.Builder requestTemplateBuilder = RequestTemplate.newBuilder();
        requestTemplateAnnotationVisitor.parseHeaders(singletonMap("headers", new String[]{"header1=value1", "header2=value2"}), requestTemplateBuilder);
        assertThat(requestTemplateBuilder.build().getRequestHeaders().getHeaders())
                .containsOnlyKeys("header1", "header2")
                .containsValues(new BasicRequestHeader("header1", "value1"), new BasicRequestHeader("header2", "value2"));
    }

    @Test
    public void extractHeader_withoutEquals() {
        assertThatThrownBy(() -> requestTemplateAnnotationVisitor.extractHeader("namevalue", RequestTemplate.newBuilder()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void extractHeader_withEmptyName() {
        assertThatThrownBy(() -> requestTemplateAnnotationVisitor.extractHeader("   =value", RequestTemplate.newBuilder()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void extractHeader_withNoName() {
        assertThatThrownBy(() -> requestTemplateAnnotationVisitor.extractHeader("=value", RequestTemplate.newBuilder()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void extractHeader_withEmptyValue() {
        assertThatThrownBy(() -> requestTemplateAnnotationVisitor.extractHeader("name=   ", RequestTemplate.newBuilder()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void extractHeader_withNoValue() {
        assertThatThrownBy(() -> requestTemplateAnnotationVisitor.extractHeader("name=", RequestTemplate.newBuilder()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void parameterAnnotationProcessing_withRequestParameter() {
        List<RequestTemplate> visit = requestTemplateAnnotationVisitor.visit(ReactiveClientWithRequestParameters.class);
        assertThat(visit)
                .hasSize(1);
        RequestTemplate requestTemplate = visit.get(0);
        assertThat(requestTemplate.getRequestParameters().getIndexToName())
                .contains(new SimpleEntry<>(0, "requestParameter1"),
                        new SimpleEntry<>(1, "requestParameter2"));
    }

    @Test
    public void parameterAnnotationProcessing_withPathParameter() {
        List<RequestTemplate> visit = requestTemplateAnnotationVisitor.visit(ReactiveClientWithPathParameters.class);
        assertThat(visit)
                .hasSize(1);
        RequestTemplate requestTemplate = visit.get(0);
        assertThat(requestTemplate.getRequestSegments().getIndexToName())
                .contains(new SimpleEntry<>(0, "pathVariable1"),
                        new SimpleEntry<>(1, "pathVariable2"));
    }

    @Test
    public void parameterAnnotewationProcessing_withRequestAndPathParameters() {
        List<RequestTemplate> visit = requestTemplateAnnotationVisitor.visit(ReactiveClientWithRequestAndPathParameters.class);
        assertThat(visit)
                .hasSize(1);
        RequestTemplate requestTemplate = visit.get(0);
        assertThat(requestTemplate.getRequestParameters().getIndexToName())
                .contains(new SimpleEntry<>(0, "requestParameter1"));
        assertThat(requestTemplate.getRequestSegments().getIndexToName())
                .contains(new SimpleEntry<>(1, "pathVariable1"));
    }

    interface SimpleInterface {
    }

    @RequestMapping("/parent")
    interface ParentReactiveClient {
    }

    @RequestMapping("/parentbis")
    interface ParentReactiveClientBis {
    }

    @RequestMapping("/child")
    interface ChildReactiveClient extends ParentReactiveClient {

        void testGet();

        @RequestMapping("/get")
        void testGet2(String test);

    }

    @RequestMapping("/grandChild")
    interface ChildReactiveClientWithTwoDirectParents extends ChildReactiveClient, ParentReactiveClient {
    }

    interface ReactiveClientWithRequestParameters {
        void testGet2(@RequestParam("requestParameter1") String requestParameter1, @RequestParam("requestParameter2") String requestParameter2);
    }

    interface ReactiveClientWithPathParameters {
        void testGet2(@PathVariable("pathVariable1") String pathVariable1, @PathVariable("pathVariable2") String pathVariable2);
    }

    interface ReactiveClientWithRequestAndPathParameters {
        void testGet2(@RequestParam("requestParameter1") String requestParameter1, @PathVariable("pathVariable1") String pathVariable1);
    }
}