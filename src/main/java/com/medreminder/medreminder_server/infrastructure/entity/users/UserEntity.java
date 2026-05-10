package com.medreminder.medreminder_server.infrastructure.entity.users;


import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.infrastructure.entity.billing.PaymentEntity;
import com.medreminder.medreminder_server.infrastructure.entity.billing.PlanEntity;
import com.medreminder.medreminder_server.infrastructure.entity.billing.SubscriptionEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;


import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PaymentEntity> payments = new ArrayList<>();

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
                      String hashPassword) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.hashPassword = hashPassword;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<ProfileEntity> getProfiles() {
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

    public List<PaymentEntity> getPayments() {
        return payments;
    }

    public void syncUserData(User domain){
        this.email = domain.getEmail();
        this.hashPassword = domain.getHashPassword();
        this.name = domain.getName();
        this.dateOfBirth = domain.getDateOfBirth();
        this.gender = domain.getGender();
    }
}
