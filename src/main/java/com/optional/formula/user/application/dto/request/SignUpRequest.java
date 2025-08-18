package com.optional.formula.user.application.dto.request;

import com.optional.formula.user.domain.entity.UserRole;
import lombok.Builder;

@Builder
public record SignUpRequest(
        String email,
        String password,
        String name,
        String nickname
) {

}
