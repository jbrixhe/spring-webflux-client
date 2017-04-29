package com.webfluxclient.utils;


import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class Types {

    private static final ResolvableType PUBLISHER_TYPE =
            ResolvableType.forClass(Publisher.class);

    private static final ResolvableType MONO_TYPE =
            ResolvableType.forClass(Mono.class);

    private static final ResolvableType FLUX_TYPE =
            ResolvableType.forClass(Flux.class);

    private static final ResolvableType DATABUFFER_PUBLISHER_TYPE =
            ResolvableType.forClassWithGenerics(Publisher.class, DataBuffer.class);

    private static final ResolvableType FORM_TYPE =
            ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, String.class);

    private static final ResolvableType RESOURCE_TYPE =
            ResolvableType.forClass(Resource.class);

    private static final ResolvableType VOID_TYPE =
            ResolvableType.forClass(void.class);

    public static boolean isPublisher(ResolvableType bodyType){
        return PUBLISHER_TYPE.isAssignableFrom(bodyType);
    }

    public static boolean isMono(ResolvableType bodyType){
        return MONO_TYPE.isAssignableFrom(bodyType);
    }

    public static boolean isFlux(ResolvableType bodyType){
        return FLUX_TYPE.isAssignableFrom(bodyType);
    }

    public static boolean isDataBufferPublisher(ResolvableType bodyType) {
        return DATABUFFER_PUBLISHER_TYPE.isAssignableFrom(bodyType);
    }

    public static boolean isFormData(ResolvableType bodyType) {
        return FORM_TYPE.isAssignableFrom(bodyType);
    }

    public static boolean isResource(ResolvableType bodyType){
        return RESOURCE_TYPE.isAssignableFrom(bodyType);
    }

    public static boolean isVoid(ResolvableType bodyType) {
        return VOID_TYPE.isAssignableFrom(bodyType);
    }
}
