package com.optional.formula.product.domain.entity;

import com.optional.formula.product.exception.ProductErrorCode;
import com.optional.formula.product.exception.ProductException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "f_products")
@Entity
public class Product {

    @Id
    @Column(name = "product_id", nullable = false, updatable = false)
    private Long productId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "quantity", nullable = false)
    private Long quantity = 0L;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_status", nullable = false, length = 30)
    private SaleStatus saleStatus = SaleStatus.DRAFT;

    @Column(name = "is_delete", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    private Long createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    private Product(
            Long productId,
            Long categoryId,
            String name,
            Long quantity,
            String description,
            SaleStatus saleStatus,
            Long createdBy
    ) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.name = name;
        this.quantity = quantity;
        this.description = description;
        this.saleStatus = saleStatus;
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
        this.updatedAt = null;
        this.updatedBy = null;
    }

    public static Product of(
            Long productId,
            Long categoryId,
            String name,
            Long quantity,
            String description,
            SaleStatus saleStatus,
            Long createdBy) {
        return new Product(productId, categoryId, name, quantity, description, saleStatus,
                createdBy);
    }

    public void decreaseQuantity(long amount) {
        if (amount <= 0) {
            throw new ProductException(ProductErrorCode.PRODUCT_QUANTITY_INVALID);
        }
        if (this.quantity == null) {
            this.quantity = 0L;
        }
        if (this.quantity < amount) {
            throw new ProductException(ProductErrorCode.PRODUCT_STOCK_INSUFFICIENT);
        }
        this.quantity = this.quantity - amount;
    }

    public void increaseQuantity(long amount) {
        if (amount <= 0) {
            throw new ProductException(ProductErrorCode.PRODUCT_QUANTITY_INVALID);
        }
        if (this.quantity == null) {
            this.quantity = 0L;
        }

        if (Long.MAX_VALUE - this.quantity < amount) {
            throw new ProductException(ProductErrorCode.PRODUCT_QUANTITY_INVALID);
        }
        this.quantity = this.quantity + amount;
    }

    public void updateCategory(Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            throw new ProductException(ProductErrorCode.PRODUCT_CATEGORY_INVALID);
        }
        if (categoryId.equals(this.categoryId)) {
            return; // no-op 최적화
        }
        this.categoryId = categoryId;
    }

    public void updateName(String name) {
        if (name == null || name.isBlank() || name.length() > 100) {
            throw new ProductException(ProductErrorCode.PRODUCT_NAME_INVALID);
        }
        String trimmed = name.trim();
        if (trimmed.equals(this.name)) {
            return; // no-op
        }
        this.name = trimmed;
    }

    public void updateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new ProductException(ProductErrorCode.PRODUCT_DESCRIPTION_INVALID);
        }
        if (description.equals(this.description)) {
            return; // no-op
        }
        this.description = description;
    }

    private void validateTransition(SaleStatus next) {
        this.saleStatus.validateTransition(next);
    }

    public void changeSaleStatus(SaleStatus next) {
        if (this.saleStatus == SaleStatus.DISCONTINUED) {
            throw new ProductException(ProductErrorCode.PRODUCT_ALREADY_DISCONTINUED);
        }
        validateTransition(next);
        this.saleStatus = next;
    }

    public void updateProduct(Long userId) {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = userId;
    }

    public void softDelete(Long userId) {
        this.isDelete = true;
    }
}
