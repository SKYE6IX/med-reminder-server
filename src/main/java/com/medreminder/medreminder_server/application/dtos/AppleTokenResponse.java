package com.medreminder.medreminder_server.application.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AppleTokenResponse(
        @JsonProperty("access_token")  String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("id_token")      String idToken,
        @JsonProperty("token_type")    String tokenType,
        @JsonProperty("expires_in")    int expiresIn
) {}
