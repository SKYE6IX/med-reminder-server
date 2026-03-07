package com.medreminder.medreminder_server.application.security;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String email) {
        super("An account with email " + email + " already exists!");
    }
}
