package com.conal.dishbuilder.util;

import com.conal.dishbuilder.context.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.conal.dishbuilder.constant.Constants.EXPIRY_TIME;

@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, String> redisTemplate;

    public String genKey(String... args) {
        UUID tenantId = TenantContextHolder.getTenantContext();
        return tenantId.toString() + String.join(":", args);
    }

    public void set(String key, String value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public void set(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MINUTES);
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value, EXPIRY_TIME, TimeUnit.MINUTES);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void remove(String key) {
        redisTemplate.delete(key);
    }

    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    public Long rightPush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    public List<String> getRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public List<String> getRange(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public void expire(String key, long timeout, TimeUnit timeUnit) {
        redisTemplate.expire(key, timeout, timeUnit);
    }

}
