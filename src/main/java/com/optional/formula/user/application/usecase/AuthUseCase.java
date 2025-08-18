package com.optional.formula.user.application.usecase;

import com.optional.formula.user.application.dto.request.SignUpRequest;
import com.optional.formula.user.application.dto.response.SignUpResponse;

public interface AuthUseCase {

    SignUpResponse signUp(SignUpRequest request);
}
