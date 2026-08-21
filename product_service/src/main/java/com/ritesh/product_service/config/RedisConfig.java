package com.ritesh.product_service.config;

import com.ritesh.product_service.dtos.response.ProductResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, ProductResponse> redisTemplate(
            RedisConnectionFactory connectionFactory
    ) {

        RedisTemplate<String, ProductResponse> template =
                new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        // Redis key → String
        template.setKeySerializer(
                new StringRedisSerializer()
        );

        // Redis value → JSON
        template.setValueSerializer(
                new GenericJackson2JsonRedisSerializer()
        );

        template.setHashKeySerializer(
                new StringRedisSerializer()
        );

        template.setHashValueSerializer(
                new GenericJackson2JsonRedisSerializer()
        );

        template.afterPropertiesSet();

        return template;
    }
}