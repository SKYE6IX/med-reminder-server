package com.medreminder.medreminder_server.application.dtos.user;

public record SocialAuthRequest(String providerId,
                                String authorizationCode,
                                String provider,
                                String jwtToken,
                                String fullName,
                                String email,
                                String timeZone) {
}