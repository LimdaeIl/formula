package com.optional.formula.common.exception;

public abstract class BaseException extends RuntimeException {

    public abstract ErrorCode getErrorCode();

    BaseException(String message) {
        super(message);
    }
}
