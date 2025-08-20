package com.optional.formula.user.application.service;

import com.optional.formula.common.exception.BusinessException;
import com.optional.formula.common.jwt.JwtProvider;
import com.optional.formula.common.snowflake.Snowflake;
import com.optional.formula.user.application.dto.request.ReissueTokenRequest;
import com.optional.formula.user.application.dto.request.SendEmailCodeRequest;
import com.optional.formula.user.application.dto.request.SignInRequest;
import com.optional.formula.user.application.dto.request.SignUpRequest;
import com.optional.formula.user.application.dto.request.VerifyEmailCodeRequest;
import com.optional.formula.user.application.dto.response.ReissueTokenResponse;
import com.optional.formula.user.application.dto.response.SendEmailCodeResponse;
import com.optional.formula.user.application.dto.response.SignInResponse;
import com.optional.formula.user.application.dto.response.SignUpResponse;
import com.optional.formula.user.application.usecase.AuthUseCase;
import com.optional.formula.user.domain.entity.User;
import com.optional.formula.user.domain.entity.UserRole;
import com.optional.formula.user.domain.repository.UserRepository;
import com.optional.formula.user.exception.UserErrorCode;
import com.optional.formula.user.exception.UserException;
import com.optional.formula.user.infrastructure.persistence.RefreshTokenRepository;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.Map;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "AuthService")
@RequiredArgsConstructor
@Service
public class AuthService implements AuthUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Snowflake snowflake = new Snowflake();
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JavaMailSender mailSender;
    private final MailTemplateService mailTemplateService;


    @Value("${spring.mail.username}")
    private String email;

    private final Integer EMAIL_CODE_TIMEOUT = 3; // Minutes

    private void existsByEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException(UserErrorCode.USER_EMAIL_DUPLICATED);
        }
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private void verifyPassword(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserException(UserErrorCode.USER_PASSWORD_INVALID);
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10)); // 0~9
        }
        return code.toString(); // 예: "037192"
    }


    private void send(String to, String code) {
        try {
            JavaMailSenderImpl senderImpl = (JavaMailSenderImpl) mailSender;
            MimeMessage mimeMessage = senderImpl.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            helper.setTo(to);
            helper.setFrom(new InternetAddress(email, "Formula"));
            helper.setSubject("[Formula] 이메일 인증 코드");

            // 타임리프 HTML 템플릿 처리
            Map<String, Object> variables = Map.of(
                    "code", code,
                    "timeout", EMAIL_CODE_TIMEOUT
            );
            String htmlContent = mailTemplateService.buildEmailContent("email/email-verification",
                    variables);
            helper.setText(htmlContent, true); // HTML 메일로 전송

            senderImpl.send(mimeMessage);

        } catch (Exception e) {
            log.error("이메일 발송 실패", e);
            throw new BusinessException(UserErrorCode.EMAIL_SEND_FAIL);
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

    @Override
    public SignInResponse signIn(SignInRequest request) {
        User user = findByEmail(request.email());
        verifyPassword(user, request.password());

        String AT = jwtProvider.generateAccessToken(user.getUserId(), user.getUserRole());
        String RT = jwtProvider.generateRefreshToken(user.getUserId());
        refreshTokenRepository.saveRefreshToken(user.getUserId(), RT,
                jwtProvider.getRefreshTokenExpiation());

        return SignInResponse.from(AT, RT);
    }

    private void verifyAccessToken(String accessToken) {
        if (accessToken.isBlank()) {
            throw new BusinessException(UserErrorCode.INVALID_TOKEN);
        }
    }

    private void verifyRefreshToken(String refreshToken) {
        if (refreshToken.isBlank()) {
            throw new BusinessException(UserErrorCode.INVALID_TOKEN);
        }
    }

    @Override
    public void logout(String accessToken) {
        verifyAccessToken(accessToken);

        long remainingMillis = jwtProvider.getRemainingTime(accessToken);
        Long userId = jwtProvider.getUserId(accessToken);

        refreshTokenRepository.setTokenBlacklist(userId, remainingMillis);
        refreshTokenRepository.deleteRefreshToken(userId);
    }

    @Override
    public ReissueTokenResponse reissueToken(String accessToken, ReissueTokenRequest request) {
        verifyAccessToken(accessToken);
        verifyRefreshToken(request.refreshToken());

        Long userId = jwtProvider.getUserId(accessToken);
        String role = jwtProvider.getRole(accessToken);

        String savedRT = refreshTokenRepository.getRefreshToken(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.INVALID_TOKEN));

        if (!savedRT.equals(request.refreshToken())) {
            throw new BusinessException(UserErrorCode.INVALID_TOKEN);
        }

        Long remainingTime = jwtProvider.getRemainingTime(savedRT);
        refreshTokenRepository.setTokenBlacklist(userId, remainingTime);
        refreshTokenRepository.deleteRefreshToken(userId);

        String AT = jwtProvider.generateAccessToken(userId, UserRole.valueOf(role));
        String RT = jwtProvider.generateRefreshToken(userId);
        refreshTokenRepository.saveRefreshToken(userId, RT,
                jwtProvider.getRefreshTokenExpiation());

        return ReissueTokenResponse.from(AT, RT);
    }

    @Override
    public void verifyEmailCode(VerifyEmailCodeRequest request) {
        String getEmailCode = refreshTokenRepository.getEmailCode(request.email());
        String inputCode = request.verifyCode().toString();

        if (inputCode.isBlank()) {
            throw new BusinessException(UserErrorCode.MALFORMED_CODE);
        }

        if (!inputCode.equals(getEmailCode)) {
            throw new BusinessException(UserErrorCode.INVALID_CODE);
        }

        refreshTokenRepository.deleteEmailCode(request.email());
    }

    @Override
    public SendEmailCodeResponse sendEmailCode(SendEmailCodeRequest request) {
        String emailCode = refreshTokenRepository.getEmailCode(request.email());
        log.info("emailCode : {}", emailCode);

        String code = generateVerificationCode();
        refreshTokenRepository.setEmailCode(request.email(), code,
                Duration.ofMinutes(EMAIL_CODE_TIMEOUT));

        send(request.email(), code);

        return new SendEmailCodeResponse("인증 코드가 전송되었습니다.");
    }


}
