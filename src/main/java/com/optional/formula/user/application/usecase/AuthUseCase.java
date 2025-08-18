package com.optional.formula.user.application.usecase;

import com.optional.formula.user.application.dto.request.ReissueTokenRequest;
import com.optional.formula.user.application.dto.request.SignInRequest;
import com.optional.formula.user.application.dto.request.SignUpRequest;
import com.optional.formula.user.application.dto.response.ReissueTokenResponse;
import com.optional.formula.user.application.dto.response.SignInResponse;
import com.optional.formula.user.application.dto.response.SignUpResponse;

public interface AuthUseCase {

    SignUpResponse signUp(SignUpRequest request);

    SignInResponse signIn(SignInRequest request);

    void logout(String accessToken);

    ReissueTokenResponse reissueToken(String accessToken, ReissueTokenRequest request);
}
