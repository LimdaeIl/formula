package com.optional.formula.product.application.dto.request;

public record UpdateProductRequest(
        Long categoryId,
        String name,
        String description
) {

}
