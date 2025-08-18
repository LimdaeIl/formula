package com.optional.formula.user.domain.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserRole {
    ADMIN,
    MANAGER,
    USER
}
