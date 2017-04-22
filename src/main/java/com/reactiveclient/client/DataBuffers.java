package com.reactiveclient.client;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.FileCopyUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class DataBuffers {

    public static String readToString(DataBuffer dataBuffer) {
        try {
            return FileCopyUtils.copyToString(new InputStreamReader(dataBuffer.asInputStream()));
        } catch (IOException e) {
            return "";
        }
    }
}
