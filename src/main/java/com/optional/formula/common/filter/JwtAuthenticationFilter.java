package com.optional.formula.common.filter;

import com.optional.formula.common.exception.BusinessException;
import com.optional.formula.common.jwt.JwtProvider;
import com.optional.formula.user.domain.entity.UserRole;
import com.optional.formula.user.exception.UserErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final JwtFilterProperties filterProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        return filterProperties.getExcludePaths().stream()
                .anyMatch(pattern -> requestURI.matches(convertAntToRegex(pattern)));
    }

    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);

            try {
                Long userId = jwtProvider.getUserId(token);
                String userRoleStr = jwtProvider.getUserRole(token);

                UserRole userRole = UserRole.valueOf(userRoleStr.toUpperCase());

                request.setAttribute("X_USER_ID", userId);
                request.setAttribute("X_USER_ROLE", userRole);

            } catch (IllegalArgumentException e) {
                throw new BusinessException(UserErrorCode.INVALID_ROLE_VALUE);
            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
                throw new BusinessException(UserErrorCode.INVALID_TOKEN);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String convertAntToRegex(String antPattern) {
        return antPattern.replace("**", ".*").replace("*", "[^/]*");
    }
}
