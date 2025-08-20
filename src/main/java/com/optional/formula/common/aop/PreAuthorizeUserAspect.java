package com.optional.formula.common.aop;

import com.optional.formula.common.exception.BusinessException;
import com.optional.formula.user.domain.entity.UserRole;
import com.optional.formula.user.exception.UserErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j(topic = "PreAuthorizeUserAspect")
@Aspect
@Component
public class PreAuthorizeUserAspect {

    private static final String USER_ROLE = "X-USER-ROLE";

    @Before("@annotation(preAuthorizeUser)")
    public void preAuthorize(JoinPoint joinPoint, PreAuthorizeUser preAuthorizeUser) {
        Set<UserRole> allowedUserRole = Set.of(preAuthorizeUser.userRole());
        UserRole currentUserRole = getCurrentUserRole();

        if (!allowedUserRole.contains(currentUserRole)) {
            log.info("접근 권한 없음: 현재 역할={}, 허용 역할={}", currentUserRole, allowedUserRole);
            throw new BusinessException(UserErrorCode.INVALID_HEADER);
        }
    }

    private UserRole getCurrentUserRole() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            log.info("ServletRequestAttributes 가 NULL 입니다.");
            throw new BusinessException(UserErrorCode.INVALID_HEADER);
        }

        HttpServletRequest request = attributes.getRequest();
        String userRole = request.getHeader(USER_ROLE);

        if (userRole == null || userRole.isEmpty()) {
            log.info("User-Role 이 비어 있습니다.");
            throw new BusinessException(UserErrorCode.INVALID_HEADER);
        }

        try {
            return UserRole.valueOf(userRole.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            log.info("유효하지 않는 User-Role: {}", userRole);
            throw new BusinessException(UserErrorCode.INVALID_HEADER);
        }
    }
}
