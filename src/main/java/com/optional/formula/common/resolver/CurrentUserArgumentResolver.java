package com.optional.formula.common.resolver;

import com.optional.formula.common.exception.BusinessException;
import com.optional.formula.common.exception.CommonErrorCode;
import com.optional.formula.user.domain.entity.UserRole;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j(topic = "CurrentUserArgumentResolver")
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String USER_ID = "X-USER-ID";
    private static final String USER_ROLE = "X-USER-ROLE";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && parameter.getParameterType().equals(CurrentUserInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        String userId = webRequest.getHeader(USER_ID);
        String userRole = webRequest.getHeader(USER_ROLE);
        
        if (userId == null || userId.isBlank() || userRole == null || userRole.isBlank()) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }

        try {
            long id = Long.parseLong(userId.trim());

            if (id <= 0) {
                throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
            }

            UserRole role = UserRole.valueOf(userRole.trim().toUpperCase(Locale.ROOT));
            return CurrentUserInfo.of(id, role);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
