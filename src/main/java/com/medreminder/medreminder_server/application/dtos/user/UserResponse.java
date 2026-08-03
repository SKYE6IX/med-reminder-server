package com.medreminder.medreminder_server.application.dtos.user;

import java.time.LocalDate;

public record UserResponse(String id, String email, String name, String dateOfBirth, String gender) {
}
