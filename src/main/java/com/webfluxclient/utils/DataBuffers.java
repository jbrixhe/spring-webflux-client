package com.webfluxclient.utils;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;

public class DataBuffers {
    
    public static String readToString(DataBuffer dataBuffer) {
        try {
            return FileCopyUtils.copyToString(new InputStreamReader(dataBuffer.asInputStream()));
        }
        catch (IOException e) {
            return e.getMessage();
        }
    }
}
