package com.dev.dungcony.commons.config;

import org.springframework.beans.factory.annotation.Value;

public class ServerConfig {
    @Value("${server.port}")
    public static int port;
    @Value("${server.host}")
    public static String host;
}
