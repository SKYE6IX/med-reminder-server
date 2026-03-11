package com.medreminder.medreminder_server.application.dtos.user;

import java.time.LocalDate;
import java.util.Optional;

public class UpdateUserCommand {

    private String email;
    private String name;
    private LocalDate dateOfBirth;
    private String gender;

    public UpdateUserCommand() {
    }

    public UpdateUserCommand(String email, String name,
                             LocalDate dateOfBirth, String gender) {
        this.email = email;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<LocalDate> getDateOfBirth() {
        return Optional.ofNullable(dateOfBirth);
    }

    public Optional<String> getGender() {
        return Optional.ofNullable(gender);
    }
}
