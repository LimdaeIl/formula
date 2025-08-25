package com.optional.formula.ProductCategory.infrastructure.persistence;

import com.optional.formula.ProductCategory.domain.entity.Category;
import com.optional.formula.ProductCategory.domain.repository.CategoryRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class JpaCategoryRepository implements CategoryRepository {

    private final SpringDataJpaCategoryRepository jpaRepository;


    public boolean existsByName(String categoryName) {
        return jpaRepository.existsByName(categoryName);
    }

    @Override
    public Optional<Category> findById(Long categoryId) {
        return jpaRepository.findById(categoryId);
    }

    @Override
    public Category save(Category category) {
        return jpaRepository.save(category);
    }

}
