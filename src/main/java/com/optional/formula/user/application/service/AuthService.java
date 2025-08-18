package com.optional.formula.user.application.service;

import com.optional.formula.common.snowflake.Snowflake;
import com.optional.formula.user.application.dto.request.SignUpRequest;
import com.optional.formula.user.application.dto.response.SignUpResponse;
import com.optional.formula.user.application.usecase.AuthUseCase;
import com.optional.formula.user.domain.entity.User;
import com.optional.formula.user.domain.repository.UserRepository;
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

    private void findByEmail(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일: 존재하지 않는 이메일입니다."));
    }

    private void existsByEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이메일: 이미 존재하는 이메일입니다.");
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
                request.name(),
                request.nickname()
        );
        userRepository.save(user);

        return SignUpResponse.from(user);
    }


}
