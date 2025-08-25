package com.optional.formula.common.filter;

import com.optional.formula.common.exception.BusinessException;
import com.optional.formula.common.exception.CommonErrorCode;
import com.optional.formula.common.jwt.JwtProvider;
import com.optional.formula.user.domain.entity.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.PathContainer;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

@Slf4j(topic = "JwtAuthenticationFilter")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final List<PathPattern> excludePatterns;
    private final PathPatternParser parser = PathPatternParser.defaultInstance;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, List<String> excludePaths) {
        this.jwtProvider = jwtProvider;
        this.excludePatterns = compilePatterns(excludePaths);
    }

    private List<PathPattern> compilePatterns(List<String> raw) {
        return raw.stream()
                .flatMap(p -> {
                    // 1) 원본 패턴
                    Stream<String> s = Stream.of(p);
                    // 2) "/**"로 끝나면 기저 경로도 추가 (예: "/api/public/**" -> "/api/public")
                    if (p.endsWith("/**")) {
                        s = Stream.concat(s, Stream.of(p.substring(0, p.length() - 3)));
                    }
                    // 3) 트레일링 "/"로 끝나면 슬래시 제거 버전도 추가 (예: "/docs/" -> "/docs")
                    if (p.endsWith("/") && p.length() > 1) {
                        s = Stream.concat(s, Stream.of(p.substring(0, p.length() - 1)));
                    }
                    return s.distinct();
                })
                .map(parser::parse)
                .toList();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = stripContextPath(request.getRequestURI(), request.getContextPath());
        PathContainer container = PathContainer.parsePath(path);
        return excludePatterns.stream().anyMatch(p -> p.matches(container));
    }

    private String stripContextPath(String uri, String ctx) {
        return (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx)) ? uri.substring(ctx.length())
                : uri;
    }

    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (!shouldNotFilter(request)) {
            String tokenByHeader = request.getHeader("Authorization");

            if (tokenByHeader == null || tokenByHeader.isEmpty()) {
                throw new BusinessException(CommonErrorCode.INVALID_HEADER);
            }

            if (!tokenByHeader.startsWith("Bearer ")) {
                throw new BusinessException(CommonErrorCode.INVALID_TOKEN);
            }

            String accessToken = tokenByHeader.substring(7);

            try {
                Long userId = jwtProvider.getUserId(accessToken);
                UserRole userRole = UserRole.valueOf(
                        jwtProvider.getUserRole(accessToken).toUpperCase());
                request.setAttribute("X_USER_ID", userId);
                request.setAttribute("X_USER_ROLE", userRole);

            } catch (IllegalArgumentException e) {
                throw new BusinessException(CommonErrorCode.INVALID_ROLE_BY_TOKEN);
            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
                throw new BusinessException(CommonErrorCode.INVALID_TOKEN);
            }
        }
        filterChain.doFilter(request, response);
    }

}
