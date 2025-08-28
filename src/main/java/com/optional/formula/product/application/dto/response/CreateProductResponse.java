package com.optional.formula.product.application.dto.response;

import com.optional.formula.product.domain.entity.Product;
import com.optional.formula.product.domain.entity.SaleStatus;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record CreateProductResponse(
        Long productId,
        Long categoryId,
        String name,
        Long quantity,
        String description,
        SaleStatus saleStatus
) {

    public static CreateProductResponse from(Product product) {
        return CreateProductResponse.builder()
                .productId(product.getProductId())
                .categoryId(product.getCategoryId())
                .name(product.getName())
                .quantity(product.getQuantity())
                .description(product.getDescription())
                .saleStatus(product.getSaleStatus())
                .build();
    }
}
