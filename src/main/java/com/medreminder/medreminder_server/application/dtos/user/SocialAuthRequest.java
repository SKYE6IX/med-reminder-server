package com.medreminder.medreminder_server.application.dtos.user;

public record SocialAuthRequest(String idToken, String provider) {
}
