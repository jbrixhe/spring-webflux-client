package com.github.jbrixhe.reactiveclient.metadata;

import com.github.jbrixhe.reactiveclient.metadata.request.RequestHeader.BasicRequestHeader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
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
    public void processRootMethodMetadata_withSingleInterface() {
        MethodMetadata requestTemplate = requestTemplateAnnotationVisitor.processTargetClass(ParentReactiveClient.class);
        assertThat(requestTemplate.getRequestTemplate().getRequestSegments().getRequestSegments())
                .extracting("segment")
                .containsExactly("parent");
    }

    @Test
    public void processRootMethodMetadata_withOneParentInterface() {
        MethodMetadata requestTemplate = requestTemplateAnnotationVisitor.processTargetClass(ChildReactiveClient.class);
        assertThat(requestTemplate.getRequestTemplate().getRequestSegments().getRequestSegments())
                .extracting("segment")
                .containsExactly("parent", "child");
    }

    @Test
    public void processRootMethodMetadata_withNoRequestMappingOnClass() {
        MethodMetadata requestTemplate = requestTemplateAnnotationVisitor.processTargetClass(SimpleInterface.class);
        assertThat(requestTemplate.getRequestTemplate().getRequestSegments().getRequestSegments())
                .isEmpty();
    }

    @Test
    public void processRootMethodMetadata_withTooManyParent() {
        assertThatThrownBy(() -> requestTemplateAnnotationVisitor.processTargetClass(ChildReactiveClientWithTwoDirectParents.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void parsePath() {
        MethodMetadata.Builder requestTemplateBuilder = MethodMetadata.newBuilder();
        requestTemplateAnnotationVisitor.parsePath(singletonMap("value", new String[]{"/api"}), requestTemplateBuilder);
        assertThat(requestTemplateBuilder.build().getRequestTemplate().getRequestSegments().getRequestSegments())
                .hasSize(1)
                .extracting("segment")
                .containsExactly("api");
    }

    @Test
    public void parsePath_withNoValue() {
        MethodMetadata.Builder requestTemplateBuilder = MethodMetadata.newBuilder();
        requestTemplateAnnotationVisitor.parsePath(singletonMap("value", new String[]{}), requestTemplateBuilder);
        assertThat(requestTemplateBuilder.build().getRequestTemplate().getRequestSegments().getRequestSegments())
                .isEmpty();
    }

    @Test
    public void parsePath_withTooManyValue() {
        assertThatThrownBy(() -> requestTemplateAnnotationVisitor.parsePath(singletonMap("value", new String[]{"/parent", "/child"}), MethodMetadata.newBuilder()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void parseMethod() {
        MethodMetadata.Builder requestTemplateBuilder = MethodMetadata.newBuilder();
        requestTemplateAnnotationVisitor.parseMethod(singletonMap("method", new RequestMethod[]{RequestMethod.GET}), requestTemplateBuilder);
        assertThat(requestTemplateBuilder.build().getRequestTemplate().getHttpMethod()).isEqualTo(HttpMethod.GET);
    }

    @Test
    public void parseMethod_withNoMethod() {
        MethodMetadata.Builder requestTemplateBuilder = MethodMetadata.newBuilder();
        requestTemplateAnnotationVisitor.parseMethod(singletonMap("method", new RequestMethod[]{}), requestTemplateBuilder);
        assertThat(requestTemplateBuilder.build().getRequestTemplate().getHttpMethod()).isEqualTo(HttpMethod.GET);
    }

    @Test
    public void parseMethod_withTooManyMethod() {
        assertThatThrownBy(() -> requestTemplateAnnotationVisitor.parseMethod(singletonMap("method", new RequestMethod[]{RequestMethod.PUT, RequestMethod.POST}), MethodMetadata.newBuilder()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void parseHeaders() {
        MethodMetadata.Builder requestTemplateBuilder = MethodMetadata.newBuilder();
        requestTemplateAnnotationVisitor.parseHeaders(singletonMap("headers", new String[]{"header1=value1", "header2=value2"}), requestTemplateBuilder);
        assertThat(requestTemplateBuilder.build().getRequestTemplate().getRequestHeaders().getHeaders())
                .containsOnlyKeys("header1", "header2")
                .containsValues(new BasicRequestHeader("header1", "value1"), new BasicRequestHeader("header2", "value2"));
    }

    @Test
    public void extractHeader_withoutEquals() {
        assertThatThrownBy(() -> requestTemplateAnnotationVisitor.extractHeader("namevalue", MethodMetadata.newBuilder()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void extractHeader_withEmptyName() {
        assertThatThrownBy(() -> requestTemplateAnnotationVisitor.extractHeader("   =value", MethodMetadata.newBuilder()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void extractHeader_withNoName() {
        assertThatThrownBy(() -> requestTemplateAnnotationVisitor.extractHeader("=value", MethodMetadata.newBuilder()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void extractHeader_withEmptyValue() {
        assertThatThrownBy(() -> requestTemplateAnnotationVisitor.extractHeader("name=   ", MethodMetadata.newBuilder()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void extractHeader_withNoValue() {
        assertThatThrownBy(() -> requestTemplateAnnotationVisitor.extractHeader("name=", MethodMetadata.newBuilder()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void parameterAnnotationProcessing_withRequestParameter() {
        List<MethodMetadata> visit = requestTemplateAnnotationVisitor.visit(ReactiveClientWithRequestParameters.class);
        assertThat(visit)
                .hasSize(1);
        MethodMetadata requestTemplate = visit.get(0);
        assertThat(requestTemplate.getRequestTemplate().getRequestParameters().getIndexToName())
                .contains(new SimpleEntry<>(0, "requestParameter1"),
                        new SimpleEntry<>(1, "requestParameter2"));
    }

    @Test
    public void parameterAnnotationProcessing_withPathParameter() {
        List<MethodMetadata> visit = requestTemplateAnnotationVisitor.visit(ReactiveClientWithPathParameters.class);
        assertThat(visit)
                .hasSize(1);
        MethodMetadata requestTemplate = visit.get(0);
        assertThat(requestTemplate.getRequestTemplate().getRequestSegments().getIndexToName())
                .contains(new SimpleEntry<>(0, "pathVariable1"),
                        new SimpleEntry<>(1, "pathVariable2"));
    }

    @Test
    public void parameterAnnotationProcessing_withRequestHeader() {
        List<MethodMetadata> visit = requestTemplateAnnotationVisitor.visit(ReactiveClientWithRequestHeaders.class);
        assertThat(visit)
                .hasSize(1);
        MethodMetadata requestTemplate = visit.get(0);
        assertThat(requestTemplate.getRequestTemplate().getRequestHeaders().getIndexToName())
                .contains(new SimpleEntry<>(0, "requestHeader1"),
                        new SimpleEntry<>(1, "requestHeader2"));
    }

    @Test
    public void parameterAnnotationProcessing_withRequestAndPathParameters() {
        List<MethodMetadata> visit = requestTemplateAnnotationVisitor.visit(ReactiveClientWithRequestAndPathParameters.class);
        assertThat(visit)
                .hasSize(1);
        MethodMetadata requestTemplate = visit.get(0);
        assertThat(requestTemplate.getRequestTemplate().getRequestParameters().getIndexToName())
                .contains(new SimpleEntry<>(0, "requestParameter1"));
        assertThat(requestTemplate.getRequestTemplate().getRequestSegments().getIndexToName())
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
        void testRequestParameters(@RequestParam("requestParameter1") String requestParameter1, @RequestParam("requestParameter2") String requestParameter2);
    }

    interface ReactiveClientWithPathParameters {
        void testPathVariable(@PathVariable("pathVariable1") String pathVariable1, @PathVariable("pathVariable2") String pathVariable2);
    }

    interface ReactiveClientWithRequestHeaders {
        void testRequestHeader(@RequestHeader("requestHeader1") String requestHeader1, @RequestHeader("requestHeader2") String requestHeader2);
    }

    interface ReactiveClientWithRequestAndPathParameters {
        void testRequestParameterAndPathVariable(@RequestParam("requestParameter1") String requestParameter1, @PathVariable("pathVariable1") String pathVariable1);
    }
}