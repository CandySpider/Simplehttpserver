package com.basic.httpserver.config;

public class HttpConfigurationExeception extends RuntimeException {
    public HttpConfigurationExeception() {
    }

    public HttpConfigurationExeception(String message) {
        super(message);
    }

    public HttpConfigurationExeception(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpConfigurationExeception(Throwable cause) {
        super(cause);
    }

}
