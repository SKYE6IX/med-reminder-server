package com.medreminder.medreminder_server.infrastructure.entity.users;


import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.PlanEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "USERS")
public class UserEntity {
    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "hash_password")
    private String hashPassword;

    private String name;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String gender;

    @Column(name = "time_zone")
    private String timeZone;

    private String provider;

    @Column(name = "provider_id", unique = true)
    private String providerId;

    @Column(name = "apple_revoke_token")
    private String appleRevokeToken;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_issued_at")
    private LocalDateTime passwordResetIssuedAt;

    @Column(name = "password_reset_redeem_at")
    private LocalDateTime passwordResetRedeemedAt;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.PERSIST
    )
    private List<RefreshTokenEntity> refreshTokens = new ArrayList<>();

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("createdAt ASC")
    private List<ProfileEntity> profiles = new ArrayList<>();

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private PlanEntity plan;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private SubscriptionEntity subscription;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public UserEntity() {}

    public UserEntity(String id,
                      String email,
                      String name,
                      String hashPassword,
                      String provider,
                      String timeZone) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.hashPassword = hashPassword;
        this.provider = provider;
        this.timeZone = timeZone;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<ProfileEntity> getProfiles() {
        return profiles;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getAppleRevokeToken() {
        return appleRevokeToken;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
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

    public PlanEntity getPlan() {
        return plan;
    }

    public SubscriptionEntity getSubscription() {
        return subscription;
    }

    public void setPlan(PlanEntity plan) {
        this.plan = plan;
    }

    public void setSubscription(SubscriptionEntity subscription) {
        this.subscription = subscription;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public void setAppleRevokeToken(String appleRevokeToken) {
        this.appleRevokeToken = appleRevokeToken;
    }

    public void updateLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public void issuePasswordResetToken(String token, String timeZone) {
        this.passwordResetToken = token;
        this.passwordResetIssuedAt = LocalDateTime.now(ZoneId.of(timeZone));
    }

    public void redeemPasswordResetToken(String timeZone) {
        this.passwordResetRedeemedAt = LocalDateTime.now(ZoneId.of(timeZone));
        this.passwordResetToken = null;
    }

    public void syncUserData(User domain){
        this.email = domain.getEmail();
        this.hashPassword = domain.getHashPassword();
        this.name = domain.getName();
        this.dateOfBirth = domain.getDateOfBirth();
        this.gender = domain.getGender();
    }
}
