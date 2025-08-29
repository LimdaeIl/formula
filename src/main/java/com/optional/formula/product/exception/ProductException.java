package com.optional.formula.product.exception;

import com.optional.formula.common.exception.BusinessException;
import com.optional.formula.common.exception.ErrorCode;

public class ProductException extends BusinessException {

    public ProductException(ErrorCode errorCode) {
        super(errorCode);
    }
}
