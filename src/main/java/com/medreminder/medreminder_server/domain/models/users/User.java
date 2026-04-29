package com.medreminder.medreminder_server.domain.models.users;

import com.medreminder.medreminder_server.application.dtos.user.UpdateUserCommand;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User  {
    private final String id;
    private String email;
    private String hashPassword;
    private String name;
    private LocalDate dateOfBirth;
    private String gender;
    private String passwordResetToken;
    private LocalDateTime passwordResetIssuedAt;
    private LocalDateTime passwordResetRedeemedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<Profile> profiles = new ArrayList<>();

    public User( String id,
                 String email,
                 String name,
                 String hashPassword ) {
        this.id = id;
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.hashPassword = Objects.requireNonNull(hashPassword, "Password cannot be null");
    }

    public User(String id, String email, String hashPassword,
                String name, LocalDate dateOfBirth,
                String gender) {
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

    public List<Profile> getProfiles() {
        return profiles;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public LocalDateTime getPasswordResetIssuedAt() {
        return passwordResetIssuedAt;
    }

    public LocalDateTime getPasswordResetRedeemedAt() {
        return passwordResetRedeemedAt;
    }

    public void updateUser(UpdateUserCommand command) {
        command.getEmail().ifPresent(this::updateEmail);
        command.getName().ifPresent(this::updateName);
        command.getDateOfBirth().ifPresent(dob -> this.dateOfBirth = LocalDate.parse(dob));
        command.getGender().ifPresent(gender -> this.gender = gender);
    }

    public void addProfiles(Profile profile) {
        this.profiles.add(profile);
        profile.addUser(this);
    }

    public void removeProfiles(Profile profile) {
        this.profiles.remove(profile);
        profile.addUser(null);
    }

    private void updateName(String newName) {
        if(newName == null || newName.isEmpty()){
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = newName;
    }

    private void updateEmail(String newEmail) {
        if(newEmail == null || newEmail.isEmpty()){
            throw new IllegalArgumentException("Email cannot be empty");
        }
        this.email = newEmail;
    }

    public void updatePassword(String newPassword) {
        if( newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Hash Password cannot be empty!");
        }
        this.hashPassword = newPassword;
    }

    public void issuePasswordResetToken(String token) {
        this.passwordResetToken = token;
        this.passwordResetIssuedAt = LocalDateTime.now();
    }

    public void redeemPasswordResetToken() {
        this.passwordResetRedeemedAt = LocalDateTime.now();
        this.passwordResetToken = null;
    }
}