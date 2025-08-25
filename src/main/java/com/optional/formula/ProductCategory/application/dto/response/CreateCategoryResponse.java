package com.optional.formula.ProductCategory.application.dto.response;

import com.optional.formula.ProductCategory.domain.entity.Category;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CreateCategoryResponse(
        Long categoryId,
        String name,
        String description,
        Boolean isDelete,
        LocalDateTime createdAt,
        Long createdBy,
        LocalDateTime updatedAt,
        Long updatedBy

) {

    public static CreateCategoryResponse from(Category category) {
        return CreateCategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .description(category.getDescription())
                .isDelete(category.getIsDelete())
                .createdAt(category.getCreatedAt())
                .createdBy(category.getCreatedBy())
                .updatedAt(category.getUpdatedAt())
                .updatedBy(category.getUpdatedBy())
                .build();
    }
}
