package com.optional.formula.user.application.dto.response;

public record ReissueTokenResponse(
        String accessToken,
        String refreshToken
) {
    public static ReissueTokenResponse from(String at, String rt) {
        return new ReissueTokenResponse(at, rt);
    }
}
