package com.optional.formula.product.application.dto.response;

import com.optional.formula.product.domain.entity.Product;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record DecreaseQuantityResponse(
        Long productId,
        String name,
        Long quantity
) {

    public static DecreaseQuantityResponse from(Product product) {

        return DecreaseQuantityResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .quantity(product.getQuantity())
                .build();
    }
}
