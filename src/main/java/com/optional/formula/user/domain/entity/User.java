package com.optional.formula.user.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PUBLIC)
@Table(name = "f_users")
@Entity
public class User {

    @Id
    @Column(name = "user_id", updatable = false, nullable = false)
    private Long userId;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 512)
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole userRole = UserRole.USER;

    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private User(Long userId, String email, String password, String nickname) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = null;
    }

    public static User of(
            Long userId,
            String email,
            String password,
            String nickname) {
        return new User(userId, email, password, nickname);
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.isDelete = true;
        this.updatedAt = LocalDateTime.now();
    }
}
