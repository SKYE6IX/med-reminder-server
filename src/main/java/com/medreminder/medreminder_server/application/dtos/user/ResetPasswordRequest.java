package com.medreminder.medreminder_server.application.dtos.user;

public record ResetPasswordRequest(String oldPassword, String newPassword) {
}
