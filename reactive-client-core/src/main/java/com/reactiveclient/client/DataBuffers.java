package com.reactiveclient.client;

import org.springframework.core.io.buffer.DataBuffer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class DataBuffers {

    public static String readToString(DataBuffer dataBuffer) {
        return new BufferedReader(new InputStreamReader(dataBuffer.asInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}
