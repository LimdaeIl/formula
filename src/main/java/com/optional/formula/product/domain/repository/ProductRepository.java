package com.optional.formula.product.domain.repository;

import com.optional.formula.product.domain.entity.Product;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product createProduct);

    Optional<Product> findById(Long productId);
}
