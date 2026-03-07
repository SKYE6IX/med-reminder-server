package com.medreminder.medreminder_server.domain.model;

import com.medreminder.medreminder_server.application.dtos.user.UpdateUserCommand;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class User  {
    private String id;
    private String email;
    private String hashPassword;
    private String name;
    private LocalDate dateOfBirth;
    private String gender;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {
    }

    public User(String id, String email, String name, String hashPassword) {
        this.id = id;
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.hashPassword = Objects.requireNonNull(hashPassword, "Password cannot be null");
    }

    public User(String id, String email, String hashPassword,
                String name, LocalDate dateOfBirth, String gender) {
        this.id = id;
        this.email = email;
        this.hashPassword = hashPassword;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    public String getId() { return id; }

    public String getEmail() { return email; }

    public String getHashPassword() {
        return hashPassword;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getGender() {
        return gender;
    }

    public void changeName(String newName) {
        if(newName == null || newName.isEmpty()){
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = newName;
    }

    public void changeEmail(String newEmail) {
        if(newEmail == null || newEmail.isEmpty()){
            throw new IllegalArgumentException("Email cannot be empty");
        }
        this.email = newEmail;
    }

    public void changePassword(String newPassword) {
        if(newPassword == null || newPassword.isEmpty()){
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.hashPassword = newPassword;
    }

    public void updateUser(UpdateUserCommand command) {
        command.getEmail().ifPresent(this::changeEmail);
        command.getHashPassword().ifPresent(this::changePassword);
        command.getName().ifPresent(this::changeName);
        command.getDateOfBirth().ifPresent(dob -> this.dateOfBirth = dob);
        command.getGender().ifPresent(gender -> this.gender = gender);
    }
}