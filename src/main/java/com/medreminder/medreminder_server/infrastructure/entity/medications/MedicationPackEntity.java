package com.medreminder.medreminder_server.infrastructure.entity.medications;

import jakarta.persistence.*;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Entity(name = "MEDICATION_PACKS")
public class MedicationPackEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    @Column(name = "total_quantity")
    private double totalQuantity;

    @Column(name = "current_quantity")
    private double currentQuantity;

    @Column(name = "notify_rule")
    private String notifyRule;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "medication_profile_id")
    private MedicationProfileEntity medicationProfile;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public MedicationPackEntity() {
    }

    public MedicationPackEntity(String id,
                                double totalQuantity,
                                String notifyRule,
                                LocalDateTime addedAt) {
        this.id = id;
        this.totalQuantity = totalQuantity;
        this.notifyRule = notifyRule;
        this.addedAt = addedAt;
    }

    public String getId() {
        return id;
    }

    public double getTotalQuantity() {
        return totalQuantity;
    }

    public double getCurrentQuantity() {
        return currentQuantity;
    }

    public String getNotifyRule() {
        return notifyRule;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }
}
