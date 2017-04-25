package com.webfluxclient.utils;


import org.springframework.core.ResolvableType;
import org.springframework.util.MultiValueMap;

public abstract class ResolvableTypeUtils {

    public static boolean isFormData(ResolvableType bodyType) {
        if (MultiValueMap.class.isAssignableFrom(bodyType.getRawClass())) {
            ResolvableType keyType = bodyType.getGeneric(0);
            ResolvableType valueType = bodyType.getGeneric(1);
            if (String.class.isAssignableFrom(keyType.getRawClass()) && String.class.isAssignableFrom(valueType.getRawClass())) {
                return true;
            }
        }

        return false;
    }
}
