package com.optional.formula.ProductCategory.exception;

import com.optional.formula.common.exception.BusinessException;
import com.optional.formula.common.exception.ErrorCode;

public class CategoryException extends BusinessException {

    public CategoryException(ErrorCode errorCode) {
        super(errorCode);
    }
}
