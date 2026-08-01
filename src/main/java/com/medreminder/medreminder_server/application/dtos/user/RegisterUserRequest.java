package com.medreminder.medreminder_server.application.dtos.user;

public record RegisterUserRequest(String email, String name, String password, String timeZone){}