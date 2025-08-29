package com.optional.formula.product.application.dto.response;

import com.optional.formula.product.domain.entity.Product;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record IncreaseQuantityResponse(
        Long productId,
        String name,
        Long quantity
) {

    public static IncreaseQuantityResponse from(Product product) {
        return IncreaseQuantityResponse
                .builder()
                .productId(product.getProductId())
                .name(product.getName())
                .quantity(product.getQuantity())
                .build();
    }
}
