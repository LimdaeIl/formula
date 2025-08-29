package com.optional.formula.product.exception;

import com.optional.formula.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ProductErrorCode implements ErrorCode {


    PRODUCT_DUPLICATED("PRODUCT-001", "상품: 이미 존재하는 상품입니다.", HttpStatus.CONFLICT),
    PRODUCT_NOT_FOUND("PRODUCT-002", "상품: 상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    PRODUCT_NAME_INVALID("PRODUCT-003", "상품: 유효하지 않은 상품명입니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_DESCRIPTION_INVALID("PRODUCT-004", "상품: 유효하지 않은 설명입니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_PRICE_INVALID("PRODUCT-005", "상품: 유효하지 않은 가격입니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_QUANTITY_INVALID("PRODUCT-006", "상품: 유효하지 않은 재고 수량입니다.", HttpStatus.BAD_REQUEST),

    PRODUCT_STOCK_INSUFFICIENT("PRODUCT-007", "상품: 재고가 부족합니다.", HttpStatus.CONFLICT),

    PRODUCT_STATUS_TRANSITION_INVALID("PRODUCT-008", "상품: 허용되지 않는 판매 상태 전이입니다.", HttpStatus.CONFLICT),
    PRODUCT_ALREADY_DISCONTINUED("PRODUCT-009", "상품: 이미 단종된 상품입니다.", HttpStatus.CONFLICT),

    PRODUCT_DELETE_FORBIDDEN("PRODUCT-010", "상품: 삭제할 수 없는 상품입니다.", HttpStatus.FORBIDDEN),
    PRODUCT_UPDATE_FORBIDDEN("PRODUCT-011", "상품: 수정이 허용되지 않습니다.", HttpStatus.FORBIDDEN),

    PRODUCT_CATEGORY_NOT_FOUND("PRODUCT-012", "상품: 연결된 카테고리를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PRODUCT_CATEGORY_INVALID("PRODUCT-013", "상품: 유효하지 않은 카테고리입니다.", HttpStatus.BAD_REQUEST),

    PRODUCT_STATUS_NULL("PRODUCT-014","상품: 상품 상태가 비어 있습니다.", HttpStatus.BAD_REQUEST);




    private final String code;
    private final String message;
    private final HttpStatus status;

    @Override
    public int getStatus() {
        return status.value();
    }
}
