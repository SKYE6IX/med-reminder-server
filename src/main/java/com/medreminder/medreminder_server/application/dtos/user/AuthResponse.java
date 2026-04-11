package com.medreminder.medreminder_server.application.dtos.user;

public record AuthResponse(String id, String email, String accessToken, String refreshToken) {
}
