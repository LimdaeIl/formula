package com.optional.formula.user.application.dto.response;

import com.optional.formula.user.domain.entity.User;
import com.optional.formula.user.domain.entity.UserRole;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record SignUpResponse(
        Long userId,
        String email,
        String nickname,
        UserRole userRole,
        boolean isDelete,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static SignUpResponse from(User user) {
        return SignUpResponse.builder()
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
