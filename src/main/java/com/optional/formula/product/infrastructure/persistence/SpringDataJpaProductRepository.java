package com.optional.formula.product.infrastructure.persistence;

import com.optional.formula.product.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaProductRepository extends JpaRepository<Product, Long> {

}
