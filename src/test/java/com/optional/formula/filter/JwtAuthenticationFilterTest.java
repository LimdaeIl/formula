package com.optional.formula.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.optional.formula.common.exception.BusinessException;
import com.optional.formula.common.filter.JwtAuthenticationFilter;
import com.optional.formula.common.filter.JwtFilterProperties;
import com.optional.formula.common.jwt.JwtProvider;
import com.optional.formula.user.domain.entity.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private FilterChain filterChain;

    private JwtFilterProperties jwtFilterProperties;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        jwtFilterProperties = new JwtFilterProperties();
        jwtFilterProperties.setIncludePaths(List.of("/api/**"));
        jwtFilterProperties.setExcludePaths(List.of("/api/public/**"));

        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtProvider, jwtFilterProperties);
    }

    @Test
    @DisplayName("제외 경로에 해당하는 요청은 필터링하지 않는다 (shouldNotFilter)")
    void shouldNotFilter_whenRequestUriIsExcluded() throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/public/test");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when & then
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 필터를 통과해서 아무 일 없이 다음 필터로 넘어간다
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("유효한 토큰일 경우 사용자 ID 및 권한 정보를 Request에 설정한다")
    void shouldFilter_andSetUserAttributes_whenValidToken()
            throws ServletException, IOException, IOException {
        // given
        String token = "valid.jwt.token";
        Long userId = 1L;
        String userRole = "USER";

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/secure");
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtProvider.getUserId(token)).thenReturn(userId);
        when(jwtProvider.getUserRole(token)).thenReturn(userRole);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(request.getAttribute("X_USER_ID")).isEqualTo(userId);
        assertThat(request.getAttribute("X_USER_ROLE")).isEqualTo(UserRole.USER);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("유효하지 않은 토큰일 경우 BusinessException 예외를 던진다")
    void shouldThrowBusinessException_whenInvalidToken() {
        // given
        String token = "invalid.token";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/secure");
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtProvider.getUserId(token)).thenThrow(new RuntimeException("유효하지 않은 토큰"));

        // then
        assertThatThrownBy(
                () -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(BusinessException.class);
    }
}
