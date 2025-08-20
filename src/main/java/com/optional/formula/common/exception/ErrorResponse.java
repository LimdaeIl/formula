package com.optional.formula.common.exception;

import java.util.List;

public record ErrorResponse(
        String code,
        String message,
        int status,
        List<ValidationError> errors
) {

    public static ErrorResponse of(ErrorCode errorCode, List<ValidationError> errors) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), errorCode.getStatus(), errors);
    }

    public static ErrorResponse of(ErrorCode errorCode, Exception error) {
        ValidationError validationError = new ValidationError(errorCode.getCode(), errorCode.getMessage());
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), errorCode.getStatus(), List.of(validationError));
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), errorCode.getStatus(), null);
    }

    public record ValidationError(String field, String reason) {
        public static ValidationError of(String field, String reason) {
            return new ValidationError(field, reason);
        }
    }
}
