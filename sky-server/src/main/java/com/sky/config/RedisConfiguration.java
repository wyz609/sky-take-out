package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Class name: RedisTemplate
 * Package: com.sky.config
 * Description:
 *  当前配置类不是必须的，因为 Spring Boot 框架会自动装配RedisTemplate对象， 但是默认的key序列化器为JdkSerializationRedisSerializer
 *  导致我们存到Redis中后的数据和原数据有差别，故设置为StringRedisSerializer序列化器
 *
 * @Create: 2025/4/25 19:55
 * @Author: jay
 * @Version: 1.0
 */

@Configuration
@Slf4j
public class RedisConfiguration{

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        log.info("开始创建redis模版对象");

        RedisTemplate redisTemplate = new RedisTemplate();
        // 设置redis的连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置redis key的序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;

    }
}

