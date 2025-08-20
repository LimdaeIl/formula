package com.optional.formula.common.aop;

import com.optional.formula.user.domain.entity.UserRole;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PreAuthorizeUser {
    UserRole[] userRole() default {
            UserRole.ADMIN,
            UserRole.MANAGER,
            UserRole.USER
    };
}
