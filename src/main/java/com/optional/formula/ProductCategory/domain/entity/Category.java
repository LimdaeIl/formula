package com.optional.formula.ProductCategory.domain.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "f_categories")
@Entity
public class Category {

    @Id
    @Column(name = "category_id", nullable = false, updatable = false)
    private Long categoryId;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    private Long createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    private Category(
            Long categoryId,
            String name,
            String description,
            Long createdBy) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
        this.updatedAt = null;
        this.updatedBy = null;
    }

    public static Category of(
            Long categoryId,
            String name,
            String description,
            Long createdBy) {
        return new Category(categoryId, name, description, createdBy);
    }

    public void updateName(String newName) {
        this.name = newName;
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }

    public void updatedAt(Long userId) {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = userId;
    }

    public void softDelete(Long userId) {
        this.isDelete = true;
    }


}
