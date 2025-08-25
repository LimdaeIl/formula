package com.optional.formula.ProductCategory.application.dto.response;

import com.optional.formula.ProductCategory.domain.entity.Category;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record GetCategoryResponse(
        String name,
        String description,
        String isDelete,
        LocalDateTime createdAt,
        Long createdBy,
        LocalDateTime updatedAt,
        Long updatedBy
) {

    public static GetCategoryResponse from(Category category) {
        return GetCategoryResponse.builder()
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .createdBy(category.getCreatedBy())
                .updatedAt(category.getUpdatedAt())
                .updatedBy(category.getUpdatedBy())
                .build();
    }
}
