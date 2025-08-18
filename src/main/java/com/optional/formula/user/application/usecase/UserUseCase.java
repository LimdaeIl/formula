package com.optional.formula.user.application.usecase;

import com.optional.formula.user.application.dto.request.UpdatePasswordUserRequest;
import com.optional.formula.user.application.dto.response.GetUserResponse;
import com.optional.formula.user.application.dto.response.UpdatePasswordUserResponse;

public interface UserUseCase {

    GetUserResponse getUser(Long userId);

    UpdatePasswordUserResponse updateUserPassword(Long userId, UpdatePasswordUserRequest request);

    void deleteUser(Long userId);
}
