package com.optional.formula.user.application.dto.request;

public record UpdatePasswordUserRequest(
        String currentPassword,
        String newPassword
) {

}
