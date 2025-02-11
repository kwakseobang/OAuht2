package com.example.oauth2jwt.global.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    // lettuce -> 비동기로 동작해 Jedis에 비해 성능이 좋기
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    //RedisTemplate 사용을 위한 추가
    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());   //connection
        redisTemplate.setKeySerializer(new StringRedisSerializer());    // key -> 직렬화 설정
        redisTemplate.setValueSerializer(new StringRedisSerializer());  // value -> 직렬화 설정
        return redisTemplate;
    }
}

// RedisTemplate에서 Key와 Value의 직렬화 방식을 StringRedisSerializer로 설정하는 이유는
// 기본적으로 Spring과 Redis 사이의 데이터 직렬화 및 역직렬화 방식이 JDK 직렬화 방식을 사용하기 때문입니다.
// JDK 직렬화 방식을 사용하면 데이터가 바이너리 형태로 저장되기 때문에, Redis CLI를 통해 데이터를 조회할 때 사람이 알아볼 수 없는 형태로 출력됩니다.
// 이를 해결하기 위해 StringRedisSerializer를 사용하여 데이터를 문자열 형태로 직렬화하고 역직렬화하도록 설정합니다.