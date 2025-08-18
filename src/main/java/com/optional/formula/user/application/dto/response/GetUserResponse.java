package com.optional.formula.user.application.dto.response;

import com.optional.formula.user.domain.entity.User;
import com.optional.formula.user.domain.entity.UserRole;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record GetUserResponse(
        Long userId,
        String email,
        String name,
        String nickname,
        UserRole userRole,
        boolean isDelete,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static GetUserResponse from(User user) {
        return GetUserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .userRole(user.getUserRole())
                .isDelete(user.getIsDelete())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
