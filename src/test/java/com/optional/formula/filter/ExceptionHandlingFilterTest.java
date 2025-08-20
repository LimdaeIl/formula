package com.optional.formula.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optional.formula.common.exception.BusinessException;
import com.optional.formula.common.exception.CommonErrorCode;
import com.optional.formula.common.exception.ErrorResponse;
import com.optional.formula.common.filter.ExceptionHandlingFilter;
import com.optional.formula.user.exception.UserErrorCode;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlingFilterTest {

    @Mock
    private FilterChain filterChain;

    private ObjectMapper objectMapper;
    private ExceptionHandlingFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        filter = new ExceptionHandlingFilter(objectMapper);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close(); // 목 리소스 정리
    }

    @Test
    @DisplayName("정상 요청은 필터를 통과한다")
    void shouldDoNothing_whenNoExceptionThrown() throws Exception {
        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("BusinessException 발생 시 ErrorResponse JSON 반환")
    void shouldHandleBusinessException_andReturnJsonResponse() throws Exception {
        // given
        BusinessException exception = new BusinessException(UserErrorCode.INVALID_HEADER);
        doThrow(exception).when(filterChain).doFilter(request, response);

        // when
        filter.doFilter(request, response, filterChain);

        // then
        String json = response.getContentAsString();
        System.out.println("응답 본문: " + json);

        assertThat(json).isNotBlank();

        ErrorResponse errorResponse = objectMapper.readValue(json, ErrorResponse.class);

        assertThat(response.getStatus()).isEqualTo(UserErrorCode.INVALID_HEADER.getStatus());
        assertThat(errorResponse.code()).isEqualTo(UserErrorCode.INVALID_HEADER.getCode());
        assertThat(errorResponse.message()).isEqualTo(UserErrorCode.INVALID_HEADER.getMessage());
        assertThat(errorResponse.status()).isEqualTo(UserErrorCode.INVALID_HEADER.getStatus());

        assertThat(errorResponse.errors()).isNull();
    }


    @Test
    @DisplayName("일반 예외 발생 시 500 에러와 기본 ErrorResponse 반환")
    void shouldHandleGenericException_andReturnInternalServerError() throws Exception {
        // given
        doThrow(new RuntimeException("예상치 못한 오류")).when(filterChain).doFilter(request, response);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        String json = response.getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(json, ErrorResponse.class);

        assertThat(response.getStatus()).isEqualTo(
                CommonErrorCode.INTERNAL_SERVER_ERROR.getStatus());
        assertThat(errorResponse.code()).isEqualTo(CommonErrorCode.INTERNAL_SERVER_ERROR.getCode());
        assertThat(errorResponse.message()).isEqualTo(
                CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        assertThat(errorResponse.status()).isEqualTo(
                CommonErrorCode.INTERNAL_SERVER_ERROR.getStatus());
        assertThat(errorResponse.errors()).isNull(); // 일반 예외는 errors 필드 null 처리
    }
}