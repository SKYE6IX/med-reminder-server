package com.medreminder.medreminder_server.infrastructure.entity.users;


import jakarta.persistence.*;
import org.hibernate.annotations.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "REFRESH_TOKENS")
public class RefreshTokenEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    @Column(name = "hash_token", unique = true, nullable = false)
    private String hashToken;

    @Column(name = "expired_at")
    private Instant expiredAt;

    private boolean revoked;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public RefreshTokenEntity() {
    }

    public RefreshTokenEntity(String id,
                              String hashToken,
                              Instant expiredAt,
                              boolean revoked, UserEntity user) {
        this.id = id;
        this.hashToken = hashToken;
        this.expiredAt = expiredAt;
        this.revoked = revoked;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public Instant getExpiredAt() {
        return expiredAt;
    }

    public String getHashToken() {
        return hashToken;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public UserEntity getUser() {
        return user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
}
