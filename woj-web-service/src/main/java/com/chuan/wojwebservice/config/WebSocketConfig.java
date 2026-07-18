package com.chuan.wojwebservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author chuan_wxy
 *
 */
@EnableWebSocket
@Configuration
public class WebSocketConfig {
    @Bean
    @ConditionalOnProperty(prefix = "websocket", name = "endpoint-exporter-enabled", havingValue = "true", matchIfMissing = true)
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}