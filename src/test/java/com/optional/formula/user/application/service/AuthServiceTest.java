package com.optional.formula.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.optional.formula.common.exception.BusinessException;
import com.optional.formula.common.jwt.JwtProvider;
import com.optional.formula.user.application.dto.request.ReissueTokenRequest;
import com.optional.formula.user.application.dto.request.SignInRequest;
import com.optional.formula.user.application.dto.request.SignUpRequest;
import com.optional.formula.user.application.dto.response.SignInResponse;
import com.optional.formula.user.application.dto.response.SignUpResponse;
import com.optional.formula.user.domain.entity.User;
import com.optional.formula.user.domain.repository.UserRepository;
import com.optional.formula.user.exception.UserErrorCode;
import com.optional.formula.user.exception.UserException;
import com.optional.formula.user.infrastructure.persistence.RefreshTokenRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthService authService;

    private SignUpRequest signUpRequest;
    private SignInRequest signInRequest;
    private ReissueTokenRequest reissueTokenRequest;

    private User testUser;
    private final Long userId = 1L;
    private final String email = "test@example.com";
    private final String rawPassword = "test-password-123";
    private final String encodedPassword = "encoded-password-123";
    private final String nickname = "testNickname1";
    private final String accessToken = "Bearer mockedAccessToken";
    private final String refreshToken = "mockedRefreshToken";

    @BeforeEach
    void setUp() {
        // 사용자 도메인 객체
        testUser = User.of(userId, email, encodedPassword, nickname);

        // DTO 객체들
        signUpRequest = SignUpRequest.builder()
                .email(email)
                .password(rawPassword)
                .nickname(nickname)
                .build();

        signInRequest = new SignInRequest(email, rawPassword);
        reissueTokenRequest = new ReissueTokenRequest(refreshToken);
    }

    @Test
    @DisplayName("회원가입에 성공한다.")
    void signUp_success() {
        // given
        given(userRepository.existsByEmail(signUpRequest.email())).willReturn(false);
        given(passwordEncoder.encode(signUpRequest.password())).willReturn(testUser.getPassword());
        given(userRepository.save(any(User.class)))
                .willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        // when
        SignUpResponse response = authService.signUp(signUpRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo(testUser.getEmail());
        assertThat(response.nickname()).isEqualTo(testUser.getNickname());

        // verify
        verify(userRepository).existsByEmail(signUpRequest.email());
        verify(passwordEncoder).encode(signUpRequest.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("중복 이메일이면 예외가 발생한다.")
    void signUp_fail_by_duplicate_email() {
        // given
        given(userRepository.existsByEmail(signUpRequest.email())).willReturn(true);

        // when
        assertThatThrownBy(() -> authService.signUp(signUpRequest))
                .isInstanceOf(UserException.class)
                .hasMessage(UserErrorCode.USER_EMAIL_DUPLICATED.getMessage());

        // verify
        verify(userRepository).existsByEmail(signUpRequest.email());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("비밀번호 불일치 시 로그인 실패한다.")
    void signIn_fail_wrong_password() {
        // given
        given(userRepository.findByEmail(email)).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.signIn(signInRequest))
                .isInstanceOf(UserException.class)
                .hasMessage(UserErrorCode.USER_PASSWORD_INVALID.getMessage());

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
        verify(jwtProvider, never()).generateAccessToken(any(), any());
        verify(jwtProvider, never()).generateRefreshToken(any());
        verify(refreshTokenRepository, never()).saveRefreshToken(any(), any(), any(Long.class));
    }


    @Test
    @DisplayName("로그인 성공 시 AT, RT를 발급한다.")
    void signIn_success() {
        // given
        String issuedAccessToken = "issued-access-token";
        String issuedRefreshToken = "issued-refresh-token";

        given(userRepository.findByEmail(email)).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(true);
        given(jwtProvider.generateAccessToken(userId, testUser.getUserRole())).willReturn(
                issuedAccessToken);
        given(jwtProvider.generateRefreshToken(userId)).willReturn(issuedRefreshToken);
        given(jwtProvider.getRefreshTokenExpiation()).willReturn(3600000L); // 1시간

        // when
        SignInResponse response = authService.signIn(signInRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo(issuedAccessToken);
        assertThat(response.refreshToken()).isEqualTo(issuedRefreshToken);

        // verify
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
        verify(jwtProvider).generateAccessToken(userId, testUser.getUserRole());
        verify(jwtProvider).generateRefreshToken(userId);
        verify(refreshTokenRepository).saveRefreshToken(userId, issuedRefreshToken, 3600000L);
    }

    @Test
    @DisplayName("유효한 AT, RT으로 토큰 재발급에 성공한다.")
    void reissueToken_success() {
        // given
        String savedRefreshToken = refreshToken; // "mockedRefreshToken"
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";
        long remainingTime = 3600000L; // 1시간

        given(jwtProvider.getUserId(accessToken)).willReturn(userId);
        given(jwtProvider.getRole(accessToken)).willReturn("USER");
        given(refreshTokenRepository.getRefreshToken(userId)).willReturn(Optional.of(savedRefreshToken));
        given(jwtProvider.getRemainingTime(savedRefreshToken)).willReturn(remainingTime);
        given(jwtProvider.generateAccessToken(userId, testUser.getUserRole())).willReturn(newAccessToken);
        given(jwtProvider.generateRefreshToken(userId)).willReturn(newRefreshToken);
        given(jwtProvider.getRefreshTokenExpiation()).willReturn(3600000L);

        // when
        var response = authService.reissueToken(accessToken, reissueTokenRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo(newAccessToken);
        assertThat(response.refreshToken()).isEqualTo(newRefreshToken);

        // verify
        verify(jwtProvider).getUserId(accessToken);
        verify(jwtProvider).getRole(accessToken);
        verify(refreshTokenRepository).getRefreshToken(userId);
        verify(jwtProvider).getRemainingTime(savedRefreshToken);
        verify(refreshTokenRepository).setTokenBlacklist(userId, remainingTime);
        verify(refreshTokenRepository).deleteRefreshToken(userId);
        verify(jwtProvider).generateAccessToken(userId, testUser.getUserRole());
        verify(jwtProvider).generateRefreshToken(userId);
        verify(refreshTokenRepository).saveRefreshToken(userId, newRefreshToken, 3600000L);
    }

    @Test
    @DisplayName("유효한 AT로 로그아웃 시 RT 삭제 및 블랙리스트 등록에 성공한다.")
    void logout_success() {
        // given
        long remainingTime = 300000L; // 5분 남았다고 가정

        given(jwtProvider.getUserId(accessToken)).willReturn(userId);
        given(jwtProvider.getRemainingTime(accessToken)).willReturn(remainingTime);

        // when
        authService.logout(accessToken);

        // then
        verify(jwtProvider).getUserId(accessToken);
        verify(jwtProvider).getRemainingTime(accessToken);
        verify(refreshTokenRepository).setTokenBlacklist(userId, remainingTime);
        verify(refreshTokenRepository).deleteRefreshToken(userId);
    }

    @Test
    @DisplayName("요청 RT와 저장된 RT가 다르면 재발급 실패 예외가 발생한다.")
    void reissueToken_fail_by_mismatched_refresh_token() {
        // given
        String wrongRefreshToken = "invalid-refresh-token";
        ReissueTokenRequest wrongRequest = new ReissueTokenRequest(wrongRefreshToken);

        given(jwtProvider.getUserId(accessToken)).willReturn(userId);
        given(jwtProvider.getRole(accessToken)).willReturn("USER");
        given(refreshTokenRepository.getRefreshToken(userId)).willReturn(Optional.of(refreshToken));

        // when & then
        assertThatThrownBy(() -> authService.reissueToken(accessToken, wrongRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage(UserErrorCode.INVALID_TOKEN.getMessage());

        // verify
        verify(refreshTokenRepository, never()).deleteRefreshToken(any());
        verify(jwtProvider, never()).generateAccessToken(any(), any());
    }

    @Test
    @DisplayName("저장된 RT가 존재하지 않으면 재발급 실패 예외가 발생한다.")
    void reissueToken_fail_when_rt_not_found() {
        // given
        given(jwtProvider.getUserId(accessToken)).willReturn(userId);
        given(jwtProvider.getRole(accessToken)).willReturn("USER");
        given(refreshTokenRepository.getRefreshToken(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.reissueToken(accessToken, reissueTokenRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage(UserErrorCode.INVALID_TOKEN.getMessage());

        // verify
        verify(refreshTokenRepository, never()).deleteRefreshToken(any());
        verify(jwtProvider, never()).generateAccessToken(any(), any());
    }

    @Test
    @DisplayName("AT가 빈 문자열이면 로그아웃 시 예외가 발생한다.")
    void logout_fail_when_token_blank() {
        // given
        String blankToken = "   ";

        // when & then
        assertThatThrownBy(() -> authService.logout(blankToken))
                .isInstanceOf(BusinessException.class)
                .hasMessage(UserErrorCode.INVALID_TOKEN.getMessage());

        // verify
        verify(jwtProvider, never()).getUserId(any());
        verify(refreshTokenRepository, never()).deleteRefreshToken(any());
    }
}
