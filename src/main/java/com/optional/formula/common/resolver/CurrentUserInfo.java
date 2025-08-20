package com.optional.formula.common.resolver;

import com.optional.formula.user.domain.entity.UserRole;

public record CurrentUserInfo(
        Long userId,
        UserRole userRole
) {

    public static CurrentUserInfo of(Long userId, UserRole userRole) {
        return new CurrentUserInfo(userId, userRole);
    }
}
