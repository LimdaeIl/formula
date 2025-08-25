package com.optional.formula.common.resolver;

import com.optional.formula.common.exception.BusinessException;
import com.optional.formula.common.exception.CommonErrorCode;
import com.optional.formula.user.domain.entity.UserRole;
import jakarta.servlet.http.HttpServletRequest;
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

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && parameter.getParameterType().equals(CurrentUserInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        Object userIdAttr = request.getAttribute("X_USER_ID");
        Object userRoleAttr = request.getAttribute("X_USER_ROLE");

        if (userIdAttr == null || userRoleAttr == null) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }
        long userId = (long) userIdAttr;
        UserRole userRole = (UserRole) userRoleAttr;

        try {
            return CurrentUserInfo.of(userId, userRole);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
