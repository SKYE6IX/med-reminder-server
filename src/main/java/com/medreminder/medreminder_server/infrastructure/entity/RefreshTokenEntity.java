package com.medreminder.medreminder_server.infrastructure.entity;


import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

@Entity(name = "REFRESH_TOKEN")
public class RefreshTokenEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    @Column(name = "hash_token")
    private String hashToken;

    @Column(name = "expired_at")
    private Instant expiredAt;

    private boolean revoked;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public RefreshTokenEntity() {
    }

    public RefreshTokenEntity(String id, String hashToken, Instant expiredAt, boolean revoked) {
        this.id = id;
        this.hashToken = hashToken;
        this.expiredAt = expiredAt;
        this.revoked = revoked;
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
}
