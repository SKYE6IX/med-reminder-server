package com.medreminder.medreminder_server.application.dtos.user;

import java.time.LocalDate;
import java.util.Optional;

public class UpdateUserCommand {

    private String email;
    private String name;
    private String dateOfBirth;
    private String gender;

    public UpdateUserCommand() {
    }

    public UpdateUserCommand(String email,
                             String name,
                             String dateOfBirth,
                             String gender) {
        this.email = email;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email)
                .filter(s -> !s.isEmpty());
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name)
                .filter(s -> !s.isEmpty());
    }

    public Optional<String> getDateOfBirth() {
        return Optional.ofNullable(dateOfBirth)
                .filter(s -> !s.isEmpty());
    }

    public Optional<String> getGender() {
        return Optional.ofNullable(gender)
                .filter(s -> !s.isEmpty());
    }
}
