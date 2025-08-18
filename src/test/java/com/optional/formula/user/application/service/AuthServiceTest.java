package com.optional.formula.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.optional.formula.user.application.dto.request.SignUpRequest;
import com.optional.formula.user.application.dto.response.SignUpResponse;
import com.optional.formula.user.domain.entity.User;
import com.optional.formula.user.domain.repository.UserRepository;
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

    @InjectMocks
    private AuthService authService;

    private SignUpRequest signUpRequest;

    @BeforeEach
    void setUp() {
        signUpRequest = SignUpRequest.builder()
                .email("test@example.com")
                .name("test")
                .password("test-password-123")
                .nickname("testNickname1")
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_success() {
        // given
        given(userRepository.existsByEmail(signUpRequest.email())).willReturn(false);
        given(passwordEncoder.encode(signUpRequest.password())).willReturn("test-password-123");
        given(userRepository.save(any(User.class)))
                .willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        // when
        SignUpResponse response = authService.signUp(signUpRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.name()).isEqualTo("test");

        // verify
        verify(userRepository).existsByEmail(signUpRequest.email());
        verify(passwordEncoder).encode("test-password-123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("중복이메일이면 예외발생")
    void signUp_fail_by_duplicate_email() {
        // given
        given(userRepository.existsByEmail(signUpRequest.email())).willReturn(true);

        // when
        assertThatThrownBy(() -> authService.signUp(signUpRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일: 이미 존재하는 이메일입니다.");

        // verify
        verify(userRepository).existsByEmail(signUpRequest.email());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
    }
}
