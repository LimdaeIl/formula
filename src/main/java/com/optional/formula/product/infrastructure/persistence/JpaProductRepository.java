package com.optional.formula.product.infrastructure.persistence;

import com.optional.formula.product.domain.entity.Product;
import com.optional.formula.product.domain.repository.ProductRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class JpaProductRepository implements ProductRepository {

    private final SpringDataJpaProductRepository jpaRepository;


    @Override
    public Product save(Product createProduct) {
        return jpaRepository.save(createProduct);
    }

    @Override
    public Optional<Product> findById(Long productId) {
        return jpaRepository.findById(productId);
    }


}
