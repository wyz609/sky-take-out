package com.sky.config;

/**
 * Class name: WebSocketConfiguration
 * Package: com.sky.config
 * Description:
 *
 * @Create: 2025/5/7 13:43
 * @Author: jay
 * @Version: 1.0
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类，用于注册WebSocket的Bean
 */
@Configuration
public class WebSocketConfiguration {
    /**
     * 创建并配置一个ServerEndpointExporter Bean
     * ServerEndpointExporter用于自动扫描并注册带有@ServerEndpoint注解的WebSocket服务器端点
     * 通过Spring容器管理这些端点，简化了WebSocket端点的配置和管理过程
     *
     * @return ServerEndpointExporter实例，用于处理WebSocket服务器端点的自动扫描和注册
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}

