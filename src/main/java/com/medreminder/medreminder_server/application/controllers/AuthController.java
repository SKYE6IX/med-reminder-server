package com.medreminder.medreminder_server.application.controllers;

import com.medreminder.medreminder_server.application.dtos.user.AuthResponse;
import com.medreminder.medreminder_server.application.dtos.user.LoginRequest;
import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
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
    ResponseEntity<?> registerUser(@RequestBody RegisterUserRequest registerUserRequest) {

        AuthResponse response = authService.registerUserWithEmail(registerUserRequest);

        return  ResponseEntity.ok(response);
    }

    @PostMapping(value = "/login")
    ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

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
    ResponseEntity<?> refreshToken(@RequestBody Map<String, String> refreshBody) {

        String refreshToken = refreshBody.get("refreshToken");

        AuthResponse response = authService.refreshToken(refreshToken);

        return  ResponseEntity.ok(response);
    }
}