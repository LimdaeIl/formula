package com.optional.formula.product.application.dto.request;

import com.optional.formula.product.domain.entity.SaleStatus;

public record CreateProductRequest(
        Long categoryId,
        String name,
        Long quantity,
        String description,
        SaleStatus saleStatus
) {

}
