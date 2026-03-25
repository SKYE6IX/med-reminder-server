package com.medreminder.medreminder_server.application.controllers;

import com.medreminder.medreminder_server.application.dtos.user.AuthResponse;
import com.medreminder.medreminder_server.application.dtos.user.LoginRequest;
import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.domain.services.users.AuthService;
import com.medreminder.medreminder_server.domain.services.users.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

    @PostMapping(value = "/refresh")
    ResponseEntity<?> refreshToken(@RequestBody Map<String, String> refreshBody) {

        String refreshToken = refreshBody.get("refreshToken");

        AuthResponse response = authService.refreshToken(refreshToken);

        return  ResponseEntity.ok(response);
    }
}
