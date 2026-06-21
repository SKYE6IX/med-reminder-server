package com.medreminder.medreminder_server.application.dtos.user;

public record ChangePasswordRequest(String oldPassword, String newPassword) {
}
