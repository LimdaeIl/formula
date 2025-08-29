package com.optional.formula.product.domain.entity;

import com.optional.formula.product.exception.ProductErrorCode;
import com.optional.formula.product.exception.ProductException;

public enum SaleStatus {
    DRAFT,        // 작성중 (검색/판매되지 않음)
    ACTIVE,       // 판매중
    PAUSED,       // 일시중지(노출되지만 구매 불가, 혹은 비노출)
    OUT_OF_STOCK, // 품절
    DISCONTINUED; // 단종

    public boolean canTransitTo(SaleStatus next) {
        return switch (this) {
            case DRAFT, PAUSED, OUT_OF_STOCK -> next == ACTIVE || next == DISCONTINUED;
            case ACTIVE -> next == PAUSED || next == OUT_OF_STOCK || next == DISCONTINUED;
            case DISCONTINUED -> false; // 단종이면 종단 상태
        };
    }

    public void validateTransition(SaleStatus next) {
        if (next == null) throw new ProductException(ProductErrorCode.PRODUCT_STATUS_NULL);
        if (this == next) return; // 필요 시 허용
        if (!canTransitTo(next)) {
            throw new ProductException(ProductErrorCode.PRODUCT_STATUS_TRANSITION_INVALID);
        }
    }
}
