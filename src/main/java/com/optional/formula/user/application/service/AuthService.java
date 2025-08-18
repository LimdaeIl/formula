package com.optional.formula.user.application.service;

import com.optional.formula.common.snowflake.Snowflake;
import com.optional.formula.user.application.dto.request.SignUpRequest;
import com.optional.formula.user.application.dto.response.SignUpResponse;
import com.optional.formula.user.application.usecase.AuthUseCase;
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
public class AuthService implements AuthUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Snowflake snowflake = new Snowflake();

    private void existsByEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException(UserErrorCode.USER_EMAIL_DUPLICATED);
        }
    }

    @Transactional
    @Override
    public SignUpResponse signUp(SignUpRequest request) {
        existsByEmail(request.email());

        User user = User.of(
                snowflake.nextId(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.nickname()
        );
        userRepository.save(user);

        return SignUpResponse.from(user);
    }
}
