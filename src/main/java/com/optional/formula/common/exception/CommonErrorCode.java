package com.optional.formula.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    INTERNAL_SERVER_ERROR("COMMON-500", "서버 내부 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT_VALUE("COMMON-400", "잘못된 입력입니다.", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED("COMMON-405", "허용되지 않은 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    ENTITY_NOT_FOUND("COMMON-404", "해당 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("COMMON-401", "인가 받지 않았습니다. 로그인이 필요합니다.", HttpStatus.UNAUTHORIZED),
    INVALID_HEADER("COMMON-401", "Authorization 헤더가 없습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("COMMON-400", "Bearer {token} 형식이어야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_ROLE_BY_TOKEN("COMMON-400", "토큰 안에 유효하지 않은 권한이 포함되어 있습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    @Override
    public int getStatus() {
        return status.value();
    }
}
