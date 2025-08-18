package com.optional.formula.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    INTERNAL_SERVER_ERROR("COMMON-500", "서버 내부 오류입니다."),
    INVALID_INPUT_VALUE("COMMON-400", "잘못된 입력입니다."),
    METHOD_NOT_ALLOWED("COMMON-405", "허용되지 않은 HTTP 메서드입니다."),
    ENTITY_NOT_FOUND("COMMON-404", "해당 리소스를 찾을 수 없습니다.");

    private final String code;
    private final String message;
}
