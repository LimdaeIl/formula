package com.optional.formula.user.domain.repository;

import java.time.Duration;
import java.util.Optional;

public interface TokenRepository {

    void saveRefreshToken(Long userId, String refreshToken, long ttlMillis);

    void setTokenBlacklist(Long userId, long ttlMillis);

    void deleteRefreshToken(Long userId);

    Optional<String> getRefreshToken(Long userId);

}
