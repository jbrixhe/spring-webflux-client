package com.github.jbrixhe.reactiveclient.request.encoding;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

class UrlParameterEncoder extends DefaultParameterEncoder {

    @Override
    protected String convertToString(Object value) {
        try {
            String valueAsString = super.convertToString(value);
            return URLEncoder.encode(String.valueOf(valueAsString), UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}