package com.optional.formula.user.presentation;

import com.optional.formula.user.application.dto.request.ReissueTokenRequest;
import com.optional.formula.user.application.dto.request.SignInRequest;
import com.optional.formula.user.application.dto.request.SignUpRequest;
import com.optional.formula.user.application.dto.response.ReissueTokenResponse;
import com.optional.formula.user.application.dto.response.SignInResponse;
import com.optional.formula.user.application.dto.response.SignUpResponse;
import com.optional.formula.user.application.usecase.AuthUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        SignUpResponse response = authUseCase.signUp(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(@Valid @RequestBody SignInRequest request) {
        SignInResponse response = authUseCase.signIn(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String accessToken
    ) {
        authUseCase.logout(accessToken);

        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping("/reissue-token")
    public ResponseEntity<ReissueTokenResponse> reissueToken(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody ReissueTokenRequest request
    ) {
        ReissueTokenResponse response = authUseCase.reissueToken(accessToken, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

}
