package com.optional.formula.ProductCategory.infrastructure.persistence;

import com.optional.formula.ProductCategory.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaCategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);
}
