package com.reactiveclient.metadata;

import com.reactiveclient.metadata.request.RequestHeader.BasicRequestHeader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(MockitoJUnitRunner.class)
public class MethodMetadataFactoryTest {
    @InjectMocks
    private MethodMetadataFactory methodMetadataFactory;

    @Test
    public void processRootMethodMetadata_withSingleInterface() {
        MethodMetadata requestTemplate = methodMetadataFactory.processTarget(ParentReactiveClient.class, URI.create(""));
        assertThat(requestTemplate.getRequestTemplate().getUriBuilder().build())
                .isEqualTo(URI.create("/parent"));
    }

    @Test
    public void processRootMethodMetadata_withOneParentInterface() {
        MethodMetadata requestTemplate = methodMetadataFactory.processTarget(ChildReactiveClient.class, URI.create(""));
        assertThat(requestTemplate.getRequestTemplate().getUriBuilder().build())
                .isEqualTo(URI.create("/parent/child"));
    }

    @Test
    public void processRootMethodMetadata_withNoRequestMappingOnClass() {
        MethodMetadata requestTemplate = methodMetadataFactory.processTarget(SimpleInterface.class, URI.create(""));
        assertThat(requestTemplate.getRequestTemplate().getUriBuilder().build())
                .isEqualTo(URI.create(""));
    }

    @Test
    public void processRootMethodMetadata_withTooManyParent() {
        assertThatThrownBy(() -> methodMetadataFactory.processTarget(ChildReactiveClientWithTwoDirectParents.class, URI.create("")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void processRootMethodMetadata_withTargetUri() {
        MethodMetadata requestTemplate = methodMetadataFactory.processTarget(ChildReactiveClient.class, URI.create("http://localhost:8080/api"));
        assertThat(requestTemplate.getRequestTemplate().getUriBuilder().build())
                .isEqualTo(URI.create("http://localhost:8080/api/parent/child"));
    }

    @Test
    public void parsePath() {
        MethodMetadata.Builder requestTemplateBuilder = MethodMetadata.newBuilder(URI.create("http://localhost:8080"));
        methodMetadataFactory.parsePath(singletonMap("value", new String[]{"/api"}), requestTemplateBuilder);
        assertThat(requestTemplateBuilder.build().getRequestTemplate().getUriBuilder().build())
                .isEqualTo(URI.create("http://localhost:8080/api"));
    }

    @Test
    public void parsePath_withNoValue() {
        MethodMetadata.Builder requestTemplateBuilder = MethodMetadata.newBuilder(URI.create("http://localhost:8080"));
        methodMetadataFactory.parsePath(singletonMap("value", new String[]{}), requestTemplateBuilder);
        assertThat(requestTemplateBuilder.build().getRequestTemplate().getUriBuilder().build())
                .isEqualTo(URI.create("http://localhost:8080"));
    }

    @Test
    public void parsePath_withTooManyValue() {
        assertThatThrownBy(() -> methodMetadataFactory.parsePath(singletonMap("value", new String[]{"/parent", "/child"}), MethodMetadata.newBuilder(URI.create(""))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void parseMethod() {
        MethodMetadata.Builder requestTemplateBuilder = MethodMetadata.newBuilder(URI.create(""));
        methodMetadataFactory.parseMethod(singletonMap("method", new RequestMethod[]{RequestMethod.GET}), requestTemplateBuilder);
        assertThat(requestTemplateBuilder.build().getRequestTemplate().getHttpMethod()).isEqualTo(HttpMethod.GET);
    }

    @Test
    public void parseMethod_withNoMethod() {
        MethodMetadata.Builder requestTemplateBuilder = MethodMetadata.newBuilder(URI.create(""));
        methodMetadataFactory.parseMethod(singletonMap("method", new RequestMethod[]{}), requestTemplateBuilder);
        assertThat(requestTemplateBuilder.build().getRequestTemplate().getHttpMethod()).isEqualTo(HttpMethod.GET);
    }

    @Test
    public void parseMethod_withTooManyMethod() {
        assertThatThrownBy(() -> methodMetadataFactory.parseMethod(singletonMap("method", new RequestMethod[]{RequestMethod.PUT, RequestMethod.POST}), MethodMetadata.newBuilder(URI.create(""))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void parseHeaders() {
        MethodMetadata.Builder requestTemplateBuilder = MethodMetadata.newBuilder(URI.create(""));
        methodMetadataFactory.parseHeaders(singletonMap("headers", new String[]{"header1=value1", "header2=value2"}), requestTemplateBuilder);
        assertThat(requestTemplateBuilder.build().getRequestTemplate().getRequestHeaders().getHeaders())
                .containsOnlyKeys("header1", "header2")
                .containsValues(new BasicRequestHeader("header1", "value1"), new BasicRequestHeader("header2", "value2"));
    }

    @Test
    public void extractHeader_withoutEquals() {
        assertThatThrownBy(() -> methodMetadataFactory.extractHeader("namevalue", MethodMetadata.newBuilder(URI.create(""))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void extractHeader_withEmptyName() {
        assertThatThrownBy(() -> methodMetadataFactory.extractHeader("   =value", MethodMetadata.newBuilder(URI.create(""))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void extractHeader_withNoName() {
        assertThatThrownBy(() -> methodMetadataFactory.extractHeader("=value", MethodMetadata.newBuilder(URI.create(""))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void extractHeader_withEmptyValue() {
        assertThatThrownBy(() -> methodMetadataFactory.extractHeader("name=   ", MethodMetadata.newBuilder(URI.create(""))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void extractHeader_withNoValue() {
        assertThatThrownBy(() -> methodMetadataFactory.extractHeader("name=", MethodMetadata.newBuilder(URI.create(""))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void parameterAnnotationProcessing_withRequestParameter() {
        List<MethodMetadata> visit = methodMetadataFactory.build(ReactiveClientWithRequestParameters.class, URI.create(""));
        assertThat(visit)
                .hasSize(1);
        MethodMetadata requestTemplate = visit.get(0);
        assertThat(requestTemplate.getRequestTemplate().getVariableIndexToName())
                .contains(new SimpleEntry<>(0, singletonList("requestParameter1")),
                        new SimpleEntry<>(1, singletonList("requestParameter2")));
    }

    @Test
    public void parameterAnnotationProcessing_withPathParameter() {
        List<MethodMetadata> visit = methodMetadataFactory.build(ReactiveClientWithPathParameters.class, URI.create(""));
        assertThat(visit)
                .hasSize(1);
        MethodMetadata requestTemplate = visit.get(0);
        assertThat(requestTemplate.getRequestTemplate().getVariableIndexToName())
                .contains(new SimpleEntry<>(0, singletonList("pathVariable1")),
                        new SimpleEntry<>(1, singletonList("pathVariable2")));
    }

    @Test
    public void parameterAnnotationProcessing_withRequestHeader() {
        List<MethodMetadata> visit = methodMetadataFactory.build(ReactiveClientWithRequestHeaders.class, URI.create(""));
        assertThat(visit)
                .hasSize(1);
        MethodMetadata requestTemplate = visit.get(0);
        assertThat(requestTemplate.getRequestTemplate().getRequestHeaders().getIndexToName())
                .contains(new SimpleEntry<>(0, "requestHeader1"),
                        new SimpleEntry<>(1, "requestHeader2"));
    }

    @Test
    public void parameterAnnotationProcessing_withRequestAndPathParameters() {
        List<MethodMetadata> visit = methodMetadataFactory.build(ReactiveClientWithRequestAndPathParameters.class, URI.create(""));
        assertThat(visit)
                .hasSize(1);
        MethodMetadata requestTemplate = visit.get(0);
        assertThat(requestTemplate.getRequestTemplate().getVariableIndexToName())
                .contains(new SimpleEntry<>(0, singletonList("requestParameter1")),
                        new SimpleEntry<>(1, singletonList("pathVariable1")));
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