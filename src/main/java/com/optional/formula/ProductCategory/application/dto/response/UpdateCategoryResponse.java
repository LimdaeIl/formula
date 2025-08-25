package com.optional.formula.ProductCategory.application.dto.response;

import com.optional.formula.ProductCategory.domain.entity.Category;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UpdateCategoryResponse(
        String name,
        String description,
        Boolean isDelete,
        LocalDateTime createdAt,
        Long createdBy,
        LocalDateTime UpdatedAt,
        Long updatedBy
) {

    public static UpdateCategoryResponse from(Category category) {
        return UpdateCategoryResponse.builder()
                .name(category.getName())
                .description(category.getDescription())
                .isDelete(category.getIsDelete())
                .createdAt(category.getCreatedAt())
                .createdBy(category.getCreatedBy())
                .UpdatedAt(category.getUpdatedAt())
                .updatedBy(category.getUpdatedBy())
                .build();
    }
}
