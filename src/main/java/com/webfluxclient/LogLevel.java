package com.webfluxclient;

public enum LogLevel {
    /** No logs. */
    NONE,

    /**
     * Logs request and response lines.
     *
     * <p>Example:
     * <pre>{@code
     * --> POST /hello
     * Content-Type: application/json
     *
     * <-- 200 OK POST /hello (22ms)
     * }</pre>
     */
    BASIC,

    /**
     * Logs request and response lines and their respective headers.
     *
     * <p>Example:
     * <pre>{@code
     * --> POST /hello
     * Content-Type: plain/text
     * Headers:
     * - Accept-Language: en
     * - Accept-Charset: UTF-8
     * --> END POST
     *
     * <-- 200 OK POST /hello (22ms)
     * Headers:
     * - Content-Type: plain/text
     * <-- END HTTP
     * }</pre>
     */
    HEADERS
}
