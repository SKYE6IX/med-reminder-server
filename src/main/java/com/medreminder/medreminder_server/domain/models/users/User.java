package com.medreminder.medreminder_server.domain.models.users;

import com.medreminder.medreminder_server.application.dtos.user.UpdateUserCommand;
import com.medreminder.medreminder_server.domain.models.subscription.Plan;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class User  {
    private final String id;
    private String email;
    private String hashPassword;
    private String name;
    private LocalDate dateOfBirth;
    private String gender;
    private String timeZone;
    private final UserProvider provider;
    private String providerId;
    private LocalDateTime lastLoginAt;
    private Plan plan;
    private final List<Profile> profiles = new ArrayList<>();

    public User(String id,
                String email,
                String name,
                String hashPassword,
                UserProvider provider,
                String timeZone) {
        this.id = id;
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.hashPassword = hashPassword;
        this.provider = provider;
        this.timeZone = timeZone;
    }

    public User(String id,
                String email,
                String hashPassword,
                String name,
                LocalDate dateOfBirth,
                String gender,
                UserProvider provider,
                String providerId,
                LocalDateTime lastLoginAt) {
        this.id = id;
        this.email = email;
        this.hashPassword = hashPassword;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.provider = provider;
        this.providerId = providerId;
        this.lastLoginAt = lastLoginAt;
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

    public String getGender() {
        return gender;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public UserProvider getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void updateUser(UpdateUserCommand command) {

        command.getEmail().ifPresent(this::updateEmail);
        command.getName().ifPresent(this::updateName);

        command.getDateOfBirth().ifPresent(dob -> this.dateOfBirth = LocalDate.parse(dob,
                DateTimeFormatter.BASIC_ISO_DATE));
        command.getGender().ifPresent(gender -> this.gender = gender);
    }

    public void addProfiles(Profile profile) {
        this.profiles.add(profile);
        profile.addUser(this);
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
}