package com.optional.formula.user.exception;

import com.optional.formula.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserErrorCode implements ErrorCode {

    USER_DUPLICATED("USER-001", "회원: 이미 존재하는 사용자입니다."),
    USER_EMAIL_DUPLICATED("USER-002", "회원: 이미 존재하는 이메일입니다."),
    USER_PASSWORD_INVALID("USER-003", "회원: 잘못된 비밀번호입니다."),
    USER_NOT_FOUND("USER-004", "회원: 사용자를 찾을 수 없습니다.");

    private final String code;
    private final String message;
}
