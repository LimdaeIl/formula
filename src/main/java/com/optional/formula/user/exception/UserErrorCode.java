package com.optional.formula.user.exception;

import com.optional.formula.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum UserErrorCode implements ErrorCode {

    USER_DUPLICATED("USER-001", "회원: 이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),
    USER_EMAIL_DUPLICATED("USER-002", "회원: 이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),
    USER_PASSWORD_INVALID("USER-003", "회원: 잘못된 비밀번호입니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("USER-004", "회원: 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    INVALID_TOKEN("USER-005", "회원: 유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_CODE("USER-006", "회원: 유효하지 않은 코드 번호입니다.", HttpStatus.BAD_REQUEST),
    MALFORMED_CODE("USER-007", "회원: 이메일 코드 번호는 6자리입니다.", HttpStatus.BAD_REQUEST),

    EMAIL_SEND_FAIL("USER-008", "회원: 이메일 전송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_HEADER("USER-009", "회원: 유효하지 않은 헤더 정보입니다.", HttpStatus.BAD_REQUEST),
    INVALID_ROLE_VALUE("USER-010", "회원: 유효하지 않은 권한입니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;

    @Override
    public int getStatus() {
        return status.value();
    }
}
