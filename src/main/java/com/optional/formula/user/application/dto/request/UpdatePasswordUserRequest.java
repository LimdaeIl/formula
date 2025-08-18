package com.optional.formula.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdatePasswordUserRequest(
        @NotBlank(message = "비밀번호: 비밀번호는 필수입니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}|\\[\\]:\";'<>?,./]).{8,}$",
                message = "비밀번호는 최소 8자 이상이며, 영문자, 숫자, 특수문자를 포함해야 합니다."
        )
        String currentPassword,

        @NotBlank(message = "비밀번호: 비밀번호는 필수입니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}|\\[\\]:\";'<>?,./]).{8,}$",
                message = "비밀번호는 최소 8자 이상이며, 영문자, 숫자, 특수문자를 포함해야 합니다."
        )
        String newPassword
) {

}
