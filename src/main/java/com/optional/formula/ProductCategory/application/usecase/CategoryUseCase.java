package com.optional.formula.ProductCategory.application.usecase;

import com.optional.formula.ProductCategory.application.dto.request.CreateCategoryRequest;
import com.optional.formula.ProductCategory.application.dto.request.UpdateCategoryRequest;
import com.optional.formula.ProductCategory.application.dto.response.CreateCategoryResponse;
import com.optional.formula.ProductCategory.application.dto.response.GetCategoryResponse;
import com.optional.formula.ProductCategory.application.dto.response.UpdateCategoryResponse;
import com.optional.formula.common.resolver.CurrentUserInfo;

public interface CategoryUseCase {

    CreateCategoryResponse createCategory(CurrentUserInfo info, CreateCategoryRequest request);

    GetCategoryResponse getCategory(CurrentUserInfo info, Long categoryId);

    UpdateCategoryResponse UpdateCategory(CurrentUserInfo info, Long categoryId,
            UpdateCategoryRequest request);

    void deleteCategory(CurrentUserInfo info, Long categoryId);
}
