package com.medreminder.medreminder_server.application.controllers;

import com.medreminder.medreminder_server.application.dtos.user.*;
import com.medreminder.medreminder_server.application.security.UserPrincipal;
import com.medreminder.medreminder_server.domain.services.users.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/register")
    ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterUserRequest registerUserRequest) {

        AuthResponse response = authService.registerUserWithEmail(registerUserRequest);

        return  ResponseEntity.ok(response);
    }

    @PostMapping(value = "/login")
    ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {

        AuthResponse response = authService.loginUserWithEmail(loginRequest);

        return  ResponseEntity.ok(response);
    }

    @PostMapping(value = "/logout")
    ResponseEntity<?> logout(@AuthenticationPrincipal UserDetails userDetails) {

        UserPrincipal userPrincipal = (UserPrincipal) userDetails;

        authService.logoutUser(userPrincipal);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/refresh")
    ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> refreshBody) {

        String refreshToken = refreshBody.get("refreshToken");

        AuthResponse response = authService.refreshToken(refreshToken);

        return  ResponseEntity.ok(response);
    }

    @PostMapping(value = "/reset-password")
    ResponseEntity<ResetPasswordResponse> resetPassword(@AuthenticationPrincipal UserDetails userDetails,
                                    @RequestBody ResetPasswordRequest resetPasswordRequest) {

        UserPrincipal userPrincipal = (UserPrincipal) userDetails;

        ResetPasswordResponse response = authService.resetPassword(userPrincipal.getId(),
                resetPasswordRequest.oldPassword(),
                resetPasswordRequest.newPassword());

        return  ResponseEntity.ok(response);
    }
}