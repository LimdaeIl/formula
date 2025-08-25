package com.optional.formula.ProductCategory.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(

        @Size(min = 1, max = 50, message = "카테고리 이름: 1~50자여야 합니다.")
        @Pattern(
                regexp = "^(?!\\s)(?!.*\\s$)(?!.*\\R).+$",
                message = "카테고리 이름: 앞/뒤 공백 및 줄바꿈을 허용하지 않습니다."
        )
        String name,

        @NotBlank(message = "카테고리 설명: 필수입니다.")
        @Size(max = 500, message = "카테고리 설명: 최대 500자까지 허용됩니다.")
        String description
) {

}
