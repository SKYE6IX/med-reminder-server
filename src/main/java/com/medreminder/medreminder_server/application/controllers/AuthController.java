package com.medreminder.medreminder_server.application.controllers;

import com.medreminder.medreminder_server.application.dtos.user.*;
import com.medreminder.medreminder_server.application.security.UserPrincipal;
import com.medreminder.medreminder_server.domain.services.users.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

    @PostMapping(value = "/social")
    ResponseEntity<AuthResponse> authorizedUserWithSocial(@RequestBody SocialAuthRequest socialAuthRequest) {

        return ResponseEntity.ok(authService.authorizeUserWithSocial(socialAuthRequest));
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

    @PostMapping(value = "/change-password")
    ResponseEntity<ResetPasswordResponse> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                    @RequestBody ChangePasswordRequest changePasswordRequest) {

        UserPrincipal userPrincipal = (UserPrincipal) userDetails;

        ResetPasswordResponse response = authService.changePassword(userPrincipal.getId(),
                changePasswordRequest.oldPassword(),
                changePasswordRequest.newPassword());

        return  ResponseEntity.ok(response);
    }

    @PostMapping(value = "/forget-password")
    ResponseEntity<ResetPasswordResponse> resetPassword(
            @RequestBody ResetPasswordRequest resetPasswordRequest ) {

        ResetPasswordResponse  response = authService
                .resetPassword(resetPasswordRequest);

        return  ResponseEntity.ok(response);
    }

    @PostMapping(value = "/forget-password/token")
    ResponseEntity<Map<String, String>> requestPasswordResetToken(
            @RequestBody Map<String, String> resetPasswordRequest) {

        Map<String, String> response = authService
                .requestPasswordResetToken(resetPasswordRequest.get("email"));

        return  ResponseEntity.ok(response);
    }

    @GetMapping(value = "/forget-password/token")
    ResponseEntity<Map<String, String>> verifyPasswordResetToken(@RequestParam String email,
                                                                 @RequestParam int token) {
        Map<String, String> response = authService
                .verifyPasswordResetToken(email, token);

        return  ResponseEntity.ok(response);
    }
}