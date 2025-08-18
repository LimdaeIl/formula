package com.optional.formula.user.application.dto.response;

public record SignInResponse(
        String accessToken,
        String refreshToken
) {

    public static SignInResponse from(String at, String rt) {
        return new SignInResponse(at, rt);
    }
}
