package com.optional.formula.product.presentation;

import com.optional.formula.common.aop.PreAuthorizeUser;
import com.optional.formula.common.resolver.CurrentUser;
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
import com.optional.formula.product.application.usecase.ProductUseCase;
import com.optional.formula.user.domain.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@RestController
public class ProductController {

    private final ProductUseCase productUseCase;

    @PreAuthorizeUser(userRole = {UserRole.ADMIN, UserRole.MANAGER})
    @PostMapping
    public ResponseEntity<CreateProductResponse> createProduct(
            @CurrentUser CurrentUserInfo info,
            @RequestBody CreateProductRequest request
    ) {
        CreateProductResponse response = productUseCase.createProduct(info, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PreAuthorizeUser(userRole = {UserRole.ADMIN, UserRole.MANAGER, UserRole.USER})
    @GetMapping("/{productId}")
    public ResponseEntity<GetProductResponse> getProduct(
            @CurrentUser CurrentUserInfo info,
            @PathVariable(name = "productId") Long productId
    ) {
        GetProductResponse response = productUseCase.getProduct(info, productId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     *
     * @param info      로그인 중인 회원 정보
     * @param productId 상품 ID
     * @param request   카테고리 ID, 이름, 설명
     * @return 상품 정보
     */
    @PreAuthorizeUser(userRole = {UserRole.ADMIN, UserRole.MANAGER})
    @PatchMapping("/{productId}")
    public ResponseEntity<UpdateProductResponse> updateProduct(
            @CurrentUser CurrentUserInfo info,
            @PathVariable(name = "productId") Long productId,
            @RequestBody UpdateProductRequest request
    ) {

        UpdateProductResponse response = productUseCase.updateProductRequest(info, productId,
                request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PreAuthorizeUser(userRole = {UserRole.ADMIN, UserRole.MANAGER, UserRole.USER})
    @PatchMapping("/{productId}/stock/increase")
    public ResponseEntity<IncreaseQuantityResponse> increaseQuantity(
            @CurrentUser CurrentUserInfo info,
            @PathVariable(name = "productId") Long productId,
            @RequestBody IncreaseQuantityRequest request
    ) {
        IncreaseQuantityResponse response = productUseCase.increaseQuantity(info, productId,
                request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PreAuthorizeUser(userRole = {UserRole.ADMIN, UserRole.MANAGER, UserRole.USER})
    @PatchMapping("/{productId}/stock/decrease")
    public ResponseEntity<DecreaseQuantityResponse> decreaseQuantity(
            @CurrentUser CurrentUserInfo info,
            @PathVariable(name = "productId") Long productId,
            @RequestBody DecreaseQuantityRequest request
    ) {
        DecreaseQuantityResponse response = productUseCase.decreaseQuantity(info, productId,
                request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PreAuthorizeUser(userRole = {UserRole.ADMIN, UserRole.MANAGER, UserRole.USER})
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> softDelete(
            @CurrentUser CurrentUserInfo info,
            @PathVariable(name = "productId") Long productId
    ) {
        productUseCase.softDelete(info, productId);

        return ResponseEntity
                .noContent()
                .build();
    }


}
