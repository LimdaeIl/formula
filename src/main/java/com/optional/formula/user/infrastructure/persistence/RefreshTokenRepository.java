package com.optional.formula.user.infrastructure.persistence;

import com.optional.formula.user.domain.repository.TokenRepository;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RefreshTokenRepository implements TokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private final String PREFIX_NAME = "user-service";

    @Override
    public void saveRefreshToken(Long userId, String refreshToken, long ttlMillis) {
        String key = PREFIX_NAME + "::RT::" + userId;
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofMillis(ttlMillis));
    }

    @Override
    public void setTokenBlacklist(Long userId, long ttlMillis) {
        String key = PREFIX_NAME + "::BL::" + userId;
        redisTemplate.opsForValue().set(key, "", Duration.ofMillis(ttlMillis));
    }

    @Override
    public void deleteRefreshToken(Long userId) {
        String key = PREFIX_NAME + "::RT::" + userId;
        redisTemplate.delete(key);
    }

    @Override
    public Optional<String> getRefreshToken(Long userId) {
        String key = PREFIX_NAME + "::RT::" + userId;
        String token = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(token);
    }

    @Override
    public void setEmailCode(String email, String code, Duration duration) {
        String key = PREFIX_NAME + "::EC::" + email;
        redisTemplate.opsForValue().set(key, code, duration);
    }

    @Override
    public String getEmailCode(String email) {
        String key = PREFIX_NAME + "::EC::" + email;
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteEmailCode(String email) {
        String key = PREFIX_NAME + "::EC::" + email;
        redisTemplate.delete(key);
    }
}
