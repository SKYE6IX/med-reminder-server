package com.medreminder.medreminder_server.infrastructure.entity.medications;

import com.medreminder.medreminder_server.domain.models.medication.MedicationPack;
import jakarta.persistence.*;
import org.hibernate.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "MEDICATION_PACKS")
public class MedicationPackEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    @Column(name = "total_quantity")
    private BigDecimal totalQuantity;

    @Column(name = "current_quantity")
    private BigDecimal currentQuantity;

    @Column(name = "notify_rule")
    private String notifyRule;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_profile_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
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
                                BigDecimal totalQuantity,
                                BigDecimal currentQuantity,
                                String notifyRule,
                                LocalDateTime startedAt,
                                String status,
                                MedicationProfileEntity medicationProfile) {
        this.id = id;
        this.totalQuantity = totalQuantity;
        this.currentQuantity = currentQuantity;
        this.notifyRule = notifyRule;
        this.startedAt = startedAt;
        this.status = status;
        this.medicationProfile = medicationProfile;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getTotalQuantity() {
        return totalQuantity;
    }

    public BigDecimal getCurrentQuantity() {
        return currentQuantity;
    }

    public String getNotifyRule() { return notifyRule; }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public String getStatus() {
        return status;
    }

    public MedicationProfileEntity getMedicationProfile() {
        return medicationProfile;
    }

    public void updateMedicationPack (MedicationPack medicationPack) {
        this.totalQuantity = medicationPack.getTotalQuantity();
        this.currentQuantity = medicationPack.getCurrentQuantity();
        this.notifyRule = medicationPack.getNotifyRule();
        this.startedAt = medicationPack.getStartedAt();
        this.endedAt = medicationPack.getEndedAt();
        this.status = medicationPack.getStatus().toString();
    }
}
