package com.optional.formula.user.application.service;

import com.optional.formula.user.application.dto.request.UpdatePasswordUserRequest;
import com.optional.formula.user.application.dto.response.GetUserResponse;
import com.optional.formula.user.application.dto.response.UpdatePasswordUserResponse;
import com.optional.formula.user.application.usecase.UserUseCase;
import com.optional.formula.user.domain.entity.User;
import com.optional.formula.user.domain.repository.UserRepository;
import com.optional.formula.user.exception.UserErrorCode;
import com.optional.formula.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService implements UserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    @Override
    public GetUserResponse getUser(Long userId) {
        User user = findById(userId);

        return GetUserResponse.builder()
                .userId(userId)
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userRole(user.getUserRole())
                .isDelete(user.getIsDelete())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Transactional
    @Override
    public UpdatePasswordUserResponse updateUserPassword(Long userId,
            UpdatePasswordUserRequest request) {
        User user = findById(userId);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new UserException(UserErrorCode.USER_PASSWORD_INVALID);
        }

        user.updatePassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        return UpdatePasswordUserResponse.from(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        User user = findById(userId);
        user.softDelete();
    }
}
