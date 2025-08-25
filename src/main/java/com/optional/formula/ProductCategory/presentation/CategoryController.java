package com.optional.formula.ProductCategory.presentation;

import com.optional.formula.ProductCategory.application.dto.request.CreateCategoryRequest;
import com.optional.formula.ProductCategory.application.dto.request.UpdateCategoryRequest;
import com.optional.formula.ProductCategory.application.dto.response.CreateCategoryResponse;
import com.optional.formula.ProductCategory.application.dto.response.GetCategoryResponse;
import com.optional.formula.ProductCategory.application.dto.response.UpdateCategoryResponse;
import com.optional.formula.ProductCategory.application.usecase.CategoryUseCase;
import com.optional.formula.common.aop.PreAuthorizeUser;
import com.optional.formula.common.resolver.CurrentUser;
import com.optional.formula.common.resolver.CurrentUserInfo;
import com.optional.formula.user.domain.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
@RestController
public class CategoryController {

    private final CategoryUseCase categoryUseCase;

    @PreAuthorizeUser(userRole = {UserRole.ADMIN, UserRole.MANAGER})
    @PostMapping
    public ResponseEntity<CreateCategoryResponse> createCategory(
            @CurrentUser CurrentUserInfo info,
            @RequestBody CreateCategoryRequest request
    ) {

        CreateCategoryResponse response = categoryUseCase.createCategory(info, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PreAuthorizeUser(userRole = {UserRole.ADMIN, UserRole.MANAGER})
    @GetMapping("/{categoryId}")
    public ResponseEntity<GetCategoryResponse> getCategory(
            @CurrentUser CurrentUserInfo info,
            @PathVariable Long categoryId
    ) {
        GetCategoryResponse response = categoryUseCase.getCategory(info, categoryId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PreAuthorizeUser(userRole = {UserRole.ADMIN, UserRole.MANAGER})
    @PatchMapping("/{categoryId}")
    public ResponseEntity<UpdateCategoryResponse> updateCategory(
            @CurrentUser CurrentUserInfo info,
            @PathVariable Long categoryId,
            @RequestBody UpdateCategoryRequest request
    ) {
        UpdateCategoryResponse response = categoryUseCase.UpdateCategory(info, categoryId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PreAuthorizeUser(userRole = {UserRole.ADMIN, UserRole.USER, UserRole.MANAGER})
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @CurrentUser CurrentUserInfo info,
            @PathVariable Long categoryId
    ) {
        categoryUseCase.deleteCategory(info, categoryId);
        return ResponseEntity
                .noContent()
                .build();
    }


}
