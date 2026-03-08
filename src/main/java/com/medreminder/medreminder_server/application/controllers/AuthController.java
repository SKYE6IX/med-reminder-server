package com.medreminder.medreminder_server.application.controllers;

import com.medreminder.medreminder_server.application.dtos.user.AuthResponse;
import com.medreminder.medreminder_server.application.dtos.user.LoginRequest;
import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.application.security.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
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
}
