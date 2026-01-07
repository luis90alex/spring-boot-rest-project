package com.restlearningjourney.store.common;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.restlearningjourney.store.products.ProductDto;
import com.restlearningjourney.store.users.UserDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.data.redis.host")
public class CacheConfig {

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(10);

    // ------------------------------------------------------------------
    // Centralized ObjectMapper for Redis serialization
    // ------------------------------------------------------------------
    @Bean
    public ObjectMapper redisObjectMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }

    // ------------------------------------------------------------------
    // CacheManager with strongly-typed serializers per cache
    // ------------------------------------------------------------------
    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            ObjectMapper redisObjectMapper) {

        RedisCacheConfiguration userCacheConfig =
                cacheConfig(serializer(UserDto.class, redisObjectMapper));

        RedisCacheConfiguration productCacheConfig =
                cacheConfig(serializer(ProductDto.class, redisObjectMapper));

        RedisCacheConfiguration productsListCacheConfig =
                cacheConfig(listSerializer(ProductDto.class, redisObjectMapper));

        return RedisCacheManager.builder(connectionFactory)
                .enableStatistics()// enable statistics to see them in actuator
                .withCacheConfiguration("users", userCacheConfig)
                .withCacheConfiguration("products", productCacheConfig)
                .withCacheConfiguration("productsAll", productsListCacheConfig)
                .build();
    }

    // ------------------------------------------------------------------
    // Base cache configuration
    // ------------------------------------------------------------------
    private RedisCacheConfiguration cacheConfig(
            Jackson2JsonRedisSerializer<?> serializer) {

        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer)
                )
                .entryTtl(DEFAULT_TTL)
                .disableCachingNullValues();
    }

    // ------------------------------------------------------------------
    // Serializer for single objects
    // ------------------------------------------------------------------
    private <T> Jackson2JsonRedisSerializer<T> serializer(
            Class<T> type,
            ObjectMapper mapper) {

        return new Jackson2JsonRedisSerializer<>(mapper, type);
    }

    // ------------------------------------------------------------------
    // Serializer for lists of objects
    // ------------------------------------------------------------------
    private <T> Jackson2JsonRedisSerializer<List<T>> listSerializer(
            Class<T> elementType,
            ObjectMapper mapper) {

        return new Jackson2JsonRedisSerializer<>(
                mapper,
                mapper.getTypeFactory()
                        .constructCollectionType(List.class, elementType)
        );
    }
}