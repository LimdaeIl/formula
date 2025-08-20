package com.optional.formula.user.presentation;

import com.optional.formula.common.aop.PreAuthorizeUser;
import com.optional.formula.common.resolver.CurrentUser;
import com.optional.formula.common.resolver.CurrentUserInfo;
import com.optional.formula.user.application.dto.request.UpdatePasswordUserRequest;
import com.optional.formula.user.application.dto.response.GetUserResponse;
import com.optional.formula.user.application.dto.response.UpdatePasswordUserResponse;
import com.optional.formula.user.application.usecase.UserUseCase;
import com.optional.formula.user.domain.entity.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    private final UserUseCase userUseCase;

    @PreAuthorizeUser(userRole = {UserRole.USER, UserRole.MANAGER, UserRole.ADMIN})
    @GetMapping("/{userId}")
    public ResponseEntity<GetUserResponse> getUser(
            @PathVariable Long userId,
            @CurrentUser CurrentUserInfo info
    ) {
        System.out.println("CurrentUserInfo.getUserId" + info.userId());
        System.out.println("CurrentUserInfo.userRole" + info.userRole());

        GetUserResponse response = userUseCase.getUser(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<UpdatePasswordUserResponse> updatePasswordUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdatePasswordUserRequest request
    ) {
        UpdatePasswordUserResponse response = userUseCase.updateUserPassword(userId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long userId
    ) {
        userUseCase.deleteUser(userId);

        return ResponseEntity
                .noContent()
                .build();
    }
}
