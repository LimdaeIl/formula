package com.optional.formula.common.jwt;

import com.optional.formula.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum JwtErrorCode implements ErrorCode {
    EXPIRED_TOKEN("JWT-001", "만료된 토큰입니다."),
    MALFORMED_TOKEN("JWT-002", "JWT 형식이 잘못되었습니다."),
    TAMPERED_TOKEN("JWT-003", "JWT 서명이 위조되었거나 무결성이 손상되었습니다."),
    NOT_FOUND_TOKEN("JWT-004", "토큰을 찾을 수 없습니다."),
    INVALID_BEARER_TOKEN("JWT-005", "유효하지 않은 토큰입니다.");

    private final String code;
    private final String message;
}
