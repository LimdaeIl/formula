package com.optional.formula.common.exception;

import com.optional.formula.common.exception.ErrorResponse.ValidationError;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j(topic = "GlobalExceptionHandler")
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<ErrorResponse> businessExceptionHandler(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("Business Exception: [{}] {}", errorCode.getCode(), errorCode.getMessage());

        HttpStatus status = switch (errorCode) {
            case CommonErrorCode.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case CommonErrorCode.ENTITY_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CommonErrorCode.METHOD_NOT_ALLOWED -> HttpStatus.METHOD_NOT_ALLOWED;
            default -> HttpStatus.BAD_REQUEST;
        };

        return ResponseEntity.status(status).body(ErrorResponse.of(errorCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e) {
        List<ValidationError> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> ErrorResponse.ValidationError.of(
                        error.getField(),
                        error.getDefaultMessage()
                )).toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(CommonErrorCode.INVALID_INPUT_VALUE, errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled Exception: [{}] {}", e.getClass().getName(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(CommonErrorCode.INTERNAL_SERVER_ERROR, e));
    }
}
