package com.optional.formula.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.optional.formula.common.exception.BusinessException;
import com.optional.formula.common.exception.CommonErrorCode;
import com.optional.formula.common.filter.JwtAuthenticationFilter;
import com.optional.formula.common.filter.JwtFilterProperties;
import com.optional.formula.common.jwt.JwtProvider;
import com.optional.formula.user.domain.entity.UserRole;
import jakarta.servlet.FilterChain;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    private JwtFilterProperties props;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        props = new JwtFilterProperties();
        props.setIncludePaths(List.of("/api/**"));        // 현재는 사용 안 함
        props.setExcludePaths(List.of("/api/public/**")); // 공개 경로

        // 변경: props 전체가 아니라 excludePaths만 전달
        filter = new JwtAuthenticationFilter(jwtProvider, props.getExcludePaths());
    }


    @ParameterizedTest
    @ValueSource(strings = {"/api/public/test", "/api/public/", "/api/public"})
    @DisplayName("excludePaths(/api/public/**) : 해당 경로는 인증 우회(필터 로직 스킵)")
    void bypass_forExcludedPublicPaths(String uri) throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", uri);
        MockHttpServletResponse res = new MockHttpServletResponse();

        // doFilter()를 호출해야 shouldNotFilter 로직이 반영됩니다.
        filter.doFilter(req, res, filterChain);

        verify(filterChain, times(1)).doFilter(req, res);
        verifyNoInteractions(jwtProvider);
    }

    @Test
    @DisplayName("단일 * 패턴(/health/*)은 슬래시를 넘지 않는다: /health/ping은 우회, /health/ping/extra는 인증 대상")
    void antSingleStar_doesNotCrossSlash() throws Exception {
        // given: 별도 프로퍼티로 새 필터 생성
        JwtFilterProperties pp = new JwtFilterProperties();
        pp.setExcludePaths(List.of("/health/*")); // 단일 레벨만 우회
        JwtAuthenticationFilter f = new JwtAuthenticationFilter(jwtProvider, pp.getExcludePaths());

        // /health/ping  → 우회
        MockHttpServletRequest req1 = new MockHttpServletRequest("GET", "/health/ping");
        MockHttpServletResponse res1 = new MockHttpServletResponse();
        f.doFilter(req1, res1, filterChain);
        verify(filterChain, times(1)).doFilter(req1, res1);

        // /health/ping/extra → 우회되지 않음(= 인증 대상) → Authorization 없으면 INVALID_HEADER 발생
        MockHttpServletRequest req2 = new MockHttpServletRequest("GET", "/health/ping/extra");
        MockHttpServletResponse res2 = new MockHttpServletResponse();

        assertThatThrownBy(() -> f.doFilter(req2, res2, filterChain))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    // CommonErrorCode에 있으면 ↓
                    assertThat(be.getErrorCode()).isEqualTo(CommonErrorCode.INVALID_HEADER);
                    // UserErrorCode로 쓰신다면 ↓ 로 교체
                    // assertThat(be.getErrorCode()).isEqualTo(UserErrorCode.INVALID_HEADER);
                });
    }


    @Test
    @DisplayName("Bearer 뒤에 공백이 여러 개여도 → substring(7) 결과에 공백 포함 → 파싱 실패 시 INVALID_TOKEN")
    void multipleSpacesAfterBearer_treatedAsInvalidToken() throws Exception {
        String weirdHeader = "Bearer    token";
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/secure");
        req.addHeader("Authorization", weirdHeader);
        MockHttpServletResponse res = new MockHttpServletResponse();

        // substring(7)은 "   token"이므로 파서가 예외 던지도록
        when(jwtProvider.getUserId("   token")).thenThrow(new RuntimeException("bad"));

        assertThatThrownBy(() -> filter.doFilter(req, res, filterChain))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(CommonErrorCode.INVALID_TOKEN);
                    // 또는 UserErrorCode.INVALID_TOKEN
                });

        verify(jwtProvider, times(1)).getUserId("   token");
        verifyNoMoreInteractions(jwtProvider);
        verifyNoInteractions(filterChain); // 실패 시 체인 호출 없음
    }

    @Test
    @DisplayName("탭 문자 등 'Bearer '가 아니면 형식 오류 → INVALID_TOKEN")
    void tabAfterBearer_isMalformed() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/secure");
        req.addHeader("Authorization", "Bearer\tabc.def"); // 탭
        MockHttpServletResponse res = new MockHttpServletResponse();

        assertThatThrownBy(() -> filter.doFilter(req, res, filterChain))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(CommonErrorCode.INVALID_TOKEN);
                });

        verifyNoInteractions(jwtProvider);
        verifyNoInteractions(filterChain);
    }

    @Test
    @DisplayName("'Bearer'만 있고 공백/토큰이 없으면 형식 오류 → INVALID_TOKEN")
    void onlyBearer_isMalformed() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/secure");
        req.addHeader("Authorization", "Bearer");
        MockHttpServletResponse res = new MockHttpServletResponse();

        assertThatThrownBy(() -> filter.doFilter(req, res, filterChain))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(CommonErrorCode.INVALID_TOKEN);
                });

        verifyNoInteractions(jwtProvider);
        verifyNoInteractions(filterChain);
    }

    // ─────────────────────────────────────
    // 3) 체인 호출 횟수 보증
    // ─────────────────────────────────────

    @Test
    @DisplayName("성공 경로에서는 체인이 정확히 1번 호출된다")
    void chainCalledOnce_onSuccess() throws Exception {
        String token = "ok.jwt";
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/secure");
        req.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse res = new MockHttpServletResponse();

        when(jwtProvider.getUserId(token)).thenReturn(10L);
        when(jwtProvider.getUserRole(token)).thenReturn("ADMIN");

        filter.doFilter(req, res, filterChain);

        verify(filterChain, times(1)).doFilter(req, res);
        assertThat(req.getAttribute("X_USER_ID")).isEqualTo(10L);
        assertThat(req.getAttribute("X_USER_ROLE")).isEqualTo(UserRole.ADMIN);
    }


    @Test
    @DisplayName("예외 발생 시 체인은 호출되지 않는다(단락)")
    void chainNotCalled_onException() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/secure");
        MockHttpServletResponse res = new MockHttpServletResponse();

        assertThatThrownBy(() -> filter.doFilter(req, res, filterChain))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(filterChain);
    }

    // ─────────────────────────────────────
    // 4) exclude 경로는 헤더가 엉망이어도 그냥 우회(필터 미적용)
    // ─────────────────────────────────────

    @Test
    @DisplayName("exclude 경로에서는 잘못된 Authorization이라도 예외 없이 통과한다")
    void excludedPath_bypassesEvenWithBadHeader() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/public/bad");
        req.addHeader("Authorization", "Bearer"); // 형식 불량이어도 우회
        MockHttpServletResponse res = new MockHttpServletResponse();

        filter.doFilter(req, res, filterChain);

        verify(filterChain, times(1)).doFilter(req, res);
        verifyNoInteractions(jwtProvider);
    }


    @Test
    @DisplayName("소문자 'bearer '는 허용되지 않음 → INVALID_TOKEN")
    void lowercaseBearer_isMalformed() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/secure");
        req.addHeader("Authorization", "bearer abc.def");
        MockHttpServletResponse res = new MockHttpServletResponse();

        assertThatThrownBy(() -> filter.doFilter(req, res, filterChain))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(CommonErrorCode.INVALID_TOKEN);
                });

        verifyNoInteractions(jwtProvider);
        verifyNoInteractions(filterChain);
    }

    @Test
    @DisplayName("excludePaths에 매칭되면 필터링을 건너뛰고 그대로 체인 통과한다")
    void bypass_whenExcludedPath() throws Exception {
        // given
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/public/test");
        MockHttpServletResponse res = new MockHttpServletResponse();

        // when
        filter.doFilter(req, res, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(req, res);
        // 내부에서 인증 시도/예외 없음
    }

    @Test
    @DisplayName("Authorization 헤더가 없으면 INVALID_HEADER로 BusinessException 발생")
    void throw_whenMissingAuthorizationHeader() {
        // given
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/secure");
        MockHttpServletResponse res = new MockHttpServletResponse();

        // when / then
        assertThatThrownBy(() -> filter.doFilter(req, res, filterChain))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    // CommonErrorCode에 정의돼 있다면 ↓
                    assertThat(be.getErrorCode()).isEqualTo(CommonErrorCode.INVALID_HEADER);
                    // 만약 UserErrorCode를 쓰신다면 아래로 바꾸세요:
                    // assertThat(be.getErrorCode()).isEqualTo(UserErrorCode.INVALID_HEADER);
                });
        verifyNoInteractions(jwtProvider);
    }

    @Test
    @DisplayName("Authorization 형식이 'Bearer '로 시작하지 않으면 INVALID_TOKEN 발생")
    void throw_whenMalformedAuthorizationHeader() {
        // given
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/secure");
        req.addHeader("Authorization", "Token abc.def.ghi"); // 잘못된 스킴
        MockHttpServletResponse res = new MockHttpServletResponse();

        // when / then
        assertThatThrownBy(() -> filter.doFilter(req, res, filterChain))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(CommonErrorCode.INVALID_TOKEN);
                    // 또는 UserErrorCode.INVALID_TOKEN (실제 선언 위치에 맞게)
                });
        verifyNoInteractions(jwtProvider);
    }

    @Test
    @DisplayName("유효한 Bearer 토큰이면 사용자 속성을 설정하고 체인을 계속 진행한다")
    void setAttributes_whenValidToken() throws Exception {
        // given
        String token = "valid.jwt.token";
        Long userId = 1L;
        String role = "USER";

        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/secure");
        req.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse res = new MockHttpServletResponse();

        when(jwtProvider.getUserId(token)).thenReturn(userId);
        when(jwtProvider.getUserRole(token)).thenReturn(role);

        // when
        filter.doFilter(req, res, filterChain);

        // then
        assertThat(req.getAttribute("X_USER_ID")).isEqualTo(userId);
        assertThat(req.getAttribute("X_USER_ROLE")).isEqualTo(UserRole.USER);
        verify(filterChain, times(1)).doFilter(req, res);
    }

    @Test
    @DisplayName("토큰 파싱 중 예외(RuntimeException 등)가 나면 INVALID_TOKEN으로 변환하여 던진다")
    void wrapAnyParsingError_asInvalidToken() {
        // given
        String token = "invalid.token";
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/secure");
        req.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse res = new MockHttpServletResponse();

        when(jwtProvider.getUserId(token)).thenThrow(new RuntimeException("invalid"));

        // when / then
        assertThatThrownBy(() -> filter.doFilter(req, res, filterChain))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(CommonErrorCode.INVALID_TOKEN);
                });
    }

    @Test
    @DisplayName("존재하지 않는 역할 문자열이면 INVALID_ROLE_BY_TOKEN 발생")
    void throw_whenInvalidRoleValue() {
        // given
        String token = "valid.token.with.invalid.role";
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/secure");
        req.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse res = new MockHttpServletResponse();

        when(jwtProvider.getUserId(token)).thenReturn(1L);
        when(jwtProvider.getUserRole(token)).thenReturn("NOT_A_ROLE");

        // when / then
        assertThatThrownBy(() -> filter.doFilter(req, res, filterChain))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(CommonErrorCode.INVALID_ROLE_BY_TOKEN);
                });
    }

    @Test
    @DisplayName("토큰은 Bearer지만 값이 비어 있거나 잘못되어도 INVALID_TOKEN으로 변환된다")
    void emptyOrBadBearerToken_treatedAsInvalidToken() {
        // given
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/secure");
        req.addHeader("Authorization", "Bearer "); // 빈 토큰
        MockHttpServletResponse res = new MockHttpServletResponse();

        // getUserId("") 호출 시 예외가 터지도록 설정(선택)
        when(jwtProvider.getUserId("")).thenThrow(new RuntimeException("empty"));

        // when / then
        assertThatThrownBy(() -> filter.doFilter(req, res, filterChain))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(CommonErrorCode.INVALID_TOKEN);
                });
    }
}
