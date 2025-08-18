package com.optional.formula.user.application.dto.response;

import com.optional.formula.user.domain.entity.User;
import com.optional.formula.user.domain.entity.UserRole;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UpdatePasswordUserResponse(
        Long userId,
        String email,
        String nickname,
        UserRole userRole,
        boolean isDelete,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static UpdatePasswordUserResponse from(User user) {
        return UpdatePasswordUserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userRole(user.getUserRole())
                .isDelete(user.getIsDelete())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
