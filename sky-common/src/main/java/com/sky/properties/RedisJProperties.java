package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Class name: RedisJProperties
 * Package: com.sky.properties
 * Description:
 *
 * @Create: 2025/4/25 19:48
 * @Author: jay
 * @Version: 1.0
 */

@Component
@ConfigurationProperties(prefix = "sky.redis")
@Data
public class RedisJProperties {

    /**
     * 配置Redis数据库连接信息
     */
    private String host;
    private int port;
    private String password;
    private int database;

}

