package com.optional.formula.product.application.usecase;

import com.optional.formula.common.resolver.CurrentUserInfo;
import com.optional.formula.product.application.dto.request.CreateProductRequest;
import com.optional.formula.product.application.dto.request.DecreaseQuantityRequest;
import com.optional.formula.product.application.dto.request.IncreaseQuantityRequest;
import com.optional.formula.product.application.dto.request.UpdateProductRequest;
import com.optional.formula.product.application.dto.response.CreateProductResponse;
import com.optional.formula.product.application.dto.response.DecreaseQuantityResponse;
import com.optional.formula.product.application.dto.response.GetProductResponse;
import com.optional.formula.product.application.dto.response.IncreaseQuantityResponse;
import com.optional.formula.product.application.dto.response.UpdateProductResponse;

public interface ProductUseCase {


    CreateProductResponse createProduct(CurrentUserInfo info, CreateProductRequest request);

    GetProductResponse getProduct(CurrentUserInfo info, Long productId);

    UpdateProductResponse updateProductRequest(CurrentUserInfo info, Long productId,
            UpdateProductRequest request);

    IncreaseQuantityResponse increaseQuantity(CurrentUserInfo info, Long productId,
            IncreaseQuantityRequest request);

    DecreaseQuantityResponse decreaseQuantity(CurrentUserInfo info, Long productId,
            DecreaseQuantityRequest request);

    void softDelete(CurrentUserInfo info, Long productId);
}
