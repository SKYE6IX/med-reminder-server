package com.medreminder.medreminder_server.application.dtos.user;

public class RegisterUserRequest {

    private String email;
    private String name;
    private String password;

    public RegisterUserRequest() {
    }

    public RegisterUserRequest(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void updatePasswordToHash(String password) {
        this.password = password;
    }
}
