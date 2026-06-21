package com.medreminder.medreminder_server.application.dtos.user;

public record ResetPasswordRequest(String email, int token, String newPassword) {
}
