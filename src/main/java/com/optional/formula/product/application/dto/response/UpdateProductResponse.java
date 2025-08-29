package com.optional.formula.product.application.dto.response;

import com.optional.formula.product.domain.entity.Product;
import com.optional.formula.product.domain.entity.SaleStatus;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record UpdateProductResponse(
        Long productId,
        Long categoryId,
        String name,
        Long quantity,
        String description,
        SaleStatus saleStatus,
        Boolean isDelete,
        LocalDateTime createAt,
        Long createdBy,
        LocalDateTime updatedAt,
        Long updatedBy
) {

    public static UpdateProductResponse from(Product product) {
        return UpdateProductResponse.builder()
                .productId(product.getProductId())
                .categoryId(product.getCategoryId())
                .name(product.getName())
                .quantity(product.getQuantity())
                .description(product.getDescription())
                .saleStatus(product.getSaleStatus())
                .isDelete(product.getIsDelete())
                .createAt(product.getCreatedAt())
                .createdBy(product.getCreatedBy())
                .updatedAt(product.getUpdatedAt())
                .updatedBy(product.getUpdatedBy())
                .build();
    }
}
