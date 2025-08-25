package com.optional.formula.ProductCategory.exception;

import com.optional.formula.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CategoryErrorCode implements ErrorCode {

    CATEGORY_DUPLICATED("CATEGORY-001", "카테고리: 이미 존재하는 카테고리입니다.", HttpStatus.CONFLICT),
    CATEGORY_NOT_FOUND("CATEGORY-002", "카테고리: 카테고리를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CATEGORY_NAME_INVALID("CATEGORY-003", "카테고리: 유효하지 않은 카테고리 이름입니다.", HttpStatus.BAD_REQUEST),
    CATEGORY_DESCRIPTION_INVALID("CATEGORY-004", "카테고리: 유효하지 않은 설명입니다.", HttpStatus.BAD_REQUEST),
    CATEGORY_DELETE_FORBIDDEN("CATEGORY-005", "카테고리: 삭제할 수 없는 카테고리입니다.", HttpStatus.FORBIDDEN),
    CATEGORY_PARENT_NOT_FOUND("CATEGORY-006", "카테고리: 부모 카테고리를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CATEGORY_STRUCTURE_INVALID("CATEGORY-007", "카테고리: 잘못된 계층 구조입니다.", HttpStatus.BAD_REQUEST);


    private final String code;
    private final String message;
    private final HttpStatus status;

    @Override
    public int getStatus() {
        return status.value();
    }
}
