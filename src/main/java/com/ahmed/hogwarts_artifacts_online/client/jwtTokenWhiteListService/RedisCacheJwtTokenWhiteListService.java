package com.ahmed.hogwarts_artifacts_online.client.jwtTokenWhiteListService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisCacheJwtTokenWhiteListService implements JwtTokenWhiteListService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void addToken(int userId, String token, long timeout, TimeUnit timeUnit) {
        String key = getKey(userId);
        redisTemplate.opsForValue().set(key, token, timeout, timeUnit);
    }

    @Override
    public String getToken(int userId) {
        String key = getKey(userId);
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void removeToken(int userId) {
        String key = getKey(userId);
        redisTemplate.delete(key);
    }

    @Override
    public boolean isTokenValid(int userId, String tokenFromUser) {
        String tokenFromRedis = getToken(userId);
        return tokenFromRedis != null && tokenFromRedis.equals(tokenFromUser);
    }

    private String getKey(int userId) {
        return"whitelist:" + userId;
    }
}
