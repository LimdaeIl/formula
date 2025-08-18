package com.optional.formula.user.application.service;

import com.optional.formula.common.exception.BusinessException;
import com.optional.formula.common.jwt.JwtProvider;
import com.optional.formula.common.snowflake.Snowflake;
import com.optional.formula.user.application.dto.request.ReissueTokenRequest;
import com.optional.formula.user.application.dto.request.SignInRequest;
import com.optional.formula.user.application.dto.request.SignUpRequest;
import com.optional.formula.user.application.dto.response.ReissueTokenResponse;
import com.optional.formula.user.application.dto.response.SignInResponse;
import com.optional.formula.user.application.dto.response.SignUpResponse;
import com.optional.formula.user.application.usecase.AuthUseCase;
import com.optional.formula.user.domain.entity.User;
import com.optional.formula.user.domain.entity.UserRole;
import com.optional.formula.user.domain.repository.UserRepository;
import com.optional.formula.user.exception.UserErrorCode;
import com.optional.formula.user.exception.UserException;
import com.optional.formula.user.infrastructure.persistence.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "AuthService")
@RequiredArgsConstructor
@Service
public class AuthService implements AuthUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Snowflake snowflake = new Snowflake();
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    private void existsByEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException(UserErrorCode.USER_EMAIL_DUPLICATED);
        }
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private void verifyPassword(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserException(UserErrorCode.USER_PASSWORD_INVALID);
        }
    }

    @Transactional
    @Override
    public SignUpResponse signUp(SignUpRequest request) {
        existsByEmail(request.email());

        User user = User.of(
                snowflake.nextId(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.nickname()
        );
        userRepository.save(user);

        return SignUpResponse.from(user);
    }

    @Override
    public SignInResponse signIn(SignInRequest request) {
        User user = findByEmail(request.email());
        verifyPassword(user, request.password());

        String AT = jwtProvider.generateAccessToken(user.getUserId(), user.getUserRole());
        String RT = jwtProvider.generateRefreshToken(user.getUserId());
        refreshTokenRepository.saveRefreshToken(user.getUserId(), RT,
                jwtProvider.getRefreshTokenExpiation());

        return SignInResponse.from(AT, RT);
    }

    private void verifyAccessToken(String accessToken) {
        if (accessToken.isBlank()) {
            throw new BusinessException(UserErrorCode.INVALID_TOKEN);
        }
    }

    private void verifyRefreshToken(String refreshToken) {
        if (refreshToken.isBlank()) {
            throw new BusinessException(UserErrorCode.INVALID_TOKEN);
        }
    }

    @Override
    public void logout(String accessToken) {
        verifyAccessToken(accessToken);

        long remainingMillis = jwtProvider.getRemainingTime(accessToken);
        Long userId = jwtProvider.getUserId(accessToken);

        refreshTokenRepository.setTokenBlacklist(userId, remainingMillis);
        refreshTokenRepository.deleteRefreshToken(userId);
    }

    @Override
    public ReissueTokenResponse reissueToken(String accessToken, ReissueTokenRequest request) {
        verifyAccessToken(accessToken);
        verifyRefreshToken(request.refreshToken());

        Long userId = jwtProvider.getUserId(accessToken);
        String role = jwtProvider.getRole(accessToken);

        String savedRT = refreshTokenRepository.getRefreshToken(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.INVALID_TOKEN));

        if (!savedRT.equals(request.refreshToken())) {
            throw new BusinessException(UserErrorCode.INVALID_TOKEN);
        }


        Long remainingTime = jwtProvider.getRemainingTime(savedRT);
        refreshTokenRepository.setTokenBlacklist(userId, remainingTime);
        refreshTokenRepository.deleteRefreshToken(userId);

        String AT = jwtProvider.generateAccessToken(userId, UserRole.valueOf(role));
        String RT = jwtProvider.generateRefreshToken(userId);
        refreshTokenRepository.saveRefreshToken(userId, RT,
                jwtProvider.getRefreshTokenExpiation());

        return ReissueTokenResponse.from(AT, RT);
    }


}
