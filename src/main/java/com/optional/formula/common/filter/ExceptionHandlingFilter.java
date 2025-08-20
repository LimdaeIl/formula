package com.optional.formula.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optional.formula.common.exception.BusinessException;
import com.optional.formula.common.exception.CommonErrorCode;
import com.optional.formula.common.exception.ErrorCode;
import com.optional.formula.common.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class ExceptionHandlingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (BusinessException e) {
            setErrorResponse(response, e.getErrorCode());
        } catch (Exception e) {
            setErrorResponse(response, CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode)
            throws IOException {
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);

        response.setStatus(errorCode.getStatus());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }
}
