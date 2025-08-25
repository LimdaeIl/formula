package com.optional.formula.ProductCategory.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateCategoryRequest(

        @NotBlank(message = "카테고리: 카테고리명은 비어 있을 수 없습니다.")
        @Pattern(regexp = "^.{1,10}$",
                message = "카테고리: 카테고리명은 1~10자 이내여야 합니다.")
        String name,

        @Pattern(regexp = "(?s)^.{0,200}$",
                message = "카테고리: 카테고리 설명은 200자 이내여야 합니다.")
        String description
) {

}
