package com.optional.formula.ProductCategory.application.service;

import com.optional.formula.ProductCategory.application.dto.request.CreateCategoryRequest;
import com.optional.formula.ProductCategory.application.dto.request.UpdateCategoryRequest;
import com.optional.formula.ProductCategory.application.dto.response.CreateCategoryResponse;
import com.optional.formula.ProductCategory.application.dto.response.GetCategoryResponse;
import com.optional.formula.ProductCategory.application.dto.response.UpdateCategoryResponse;
import com.optional.formula.ProductCategory.application.usecase.CategoryUseCase;
import com.optional.formula.ProductCategory.domain.entity.Category;
import com.optional.formula.ProductCategory.domain.repository.CategoryRepository;
import com.optional.formula.ProductCategory.exception.CategoryErrorCode;
import com.optional.formula.ProductCategory.exception.CategoryException;
import com.optional.formula.common.resolver.CurrentUserInfo;
import com.optional.formula.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CategoryService implements CategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final Snowflake snowflake = new Snowflake();

    private void checkByCategoryName(String categoryName) {
        if (categoryRepository.existsByName(categoryName)) {
            throw new CategoryException(CategoryErrorCode.CATEGORY_DUPLICATED);
        }
    }

    private Category findByCategoryId(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND));
    }

    @Transactional
    @Override
    public CreateCategoryResponse createCategory(
            CurrentUserInfo info,
            CreateCategoryRequest request) {
        checkByCategoryName(request.name());

        Category createdCategory = Category.of(
                snowflake.nextId(),
                request.name(),
                request.description(),
                info.userId()
        );

        Category savedCategory = categoryRepository.save(createdCategory);

        return CreateCategoryResponse.from(savedCategory);
    }

    @Transactional(readOnly = true)
    @Override
    public GetCategoryResponse getCategory(CurrentUserInfo info, Long categoryId) {
        Category category = findByCategoryId(categoryId);

        return GetCategoryResponse.from(category);
    }

    @Transactional
    @Override
    public UpdateCategoryResponse UpdateCategory(CurrentUserInfo info, Long categoryId,
            UpdateCategoryRequest request) {
        Category category = findByCategoryId(categoryId);

        category.updateName(request.name());
        category.updateDescription(request.description());
        category.updateCategory(info.userId());

        Category savedCategory = categoryRepository.save(category);

        return UpdateCategoryResponse.from(savedCategory);
    }

    @Transactional
    @Override
    public void deleteCategory(CurrentUserInfo info, Long categoryId) {
        Category category = findByCategoryId(categoryId);

        category.softDelete(info.userId());
        category.updateCategory(info.userId());

        categoryRepository.save(category);
    }
}
