package com.optional.formula.product.application.service;

import com.optional.formula.ProductCategory.domain.repository.CategoryRepository;
import com.optional.formula.common.resolver.CurrentUserInfo;
import com.optional.formula.common.snowflake.Snowflake;
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
import com.optional.formula.product.domain.entity.Product;
import com.optional.formula.product.domain.repository.ProductRepository;
import com.optional.formula.product.exception.ProductErrorCode;
import com.optional.formula.product.exception.ProductException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProductService implements ProductUseCase {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final Snowflake snowflake = new Snowflake();

    private void findCategoryById(Long categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(
                        () -> new ProductException(ProductErrorCode.PRODUCT_CATEGORY_NOT_FOUND));
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional
    @Override
    public CreateProductResponse createProduct(CurrentUserInfo info, CreateProductRequest request) {
        findCategoryById(request.categoryId());

        Product createProduct = Product.of(
                snowflake.nextId(),
                request.categoryId(),
                request.name(),
                request.quantity(),
                request.description(),
                request.saleStatus(),
                info.userId()
        );

        Product savedProduct = productRepository.save(createProduct);

        return CreateProductResponse.from(savedProduct);
    }

    @Transactional(readOnly = true)
    @Override
    public GetProductResponse getProduct(CurrentUserInfo info, Long productId) {
        Product productById = findProductById(productId);

        return GetProductResponse.from(productById);
    }

    @Transactional
    @Override
    public UpdateProductResponse updateProductRequest(
            CurrentUserInfo info,
            Long productId,
            UpdateProductRequest request) {

        Product productById = findProductById(productId);

        if (request.categoryId() != null && !request.categoryId().equals(productId)) {
            findCategoryById(request.categoryId());
            productById.updateCategory(request.categoryId());
        }

        if (request.name() != null && !request.name().isBlank()) {
            productById.updateName(request.name());
        }

        if (request.description() != null && !request.description().isBlank()) {
            productById.updateDescription(request.description());
        }

        productById.updateProduct(info.userId());
        Product savedProduct = productRepository.save(productById);

        return UpdateProductResponse.from(savedProduct);
    }

    @Transactional
    @Override
    public IncreaseQuantityResponse increaseQuantity(
            CurrentUserInfo info,
            Long productId,
            IncreaseQuantityRequest request) {
        Product productById = findProductById(productId);

        productById.increaseQuantity(request.amount());

//        productRepository.save(productById);

        return IncreaseQuantityResponse.from(productById);
    }

    @Transactional
    @Override
    public DecreaseQuantityResponse decreaseQuantity(
            CurrentUserInfo info,
            Long productId,
            DecreaseQuantityRequest request) {
        Product productById = findProductById(productId);

        productById.decreaseQuantity(request.amount());

        //        productRepository.save(productById);

        return DecreaseQuantityResponse.from(productById);
    }

    @Transactional
    @Override
    public void softDelete(CurrentUserInfo info, Long productId) {
        Product productById = findProductById(productId);
        productById.softDelete(info.userId());
        productById.updateProduct(info.userId());
    }
}
