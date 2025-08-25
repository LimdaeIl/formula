package com.optional.formula.ProductCategory.domain.repository;

import com.optional.formula.ProductCategory.domain.entity.Category;
import java.util.Optional;

public interface CategoryRepository {

    boolean existsByName(String categoryName);

    Optional<Category> findById(Long categoryId);
    Category save(Category category);
}
