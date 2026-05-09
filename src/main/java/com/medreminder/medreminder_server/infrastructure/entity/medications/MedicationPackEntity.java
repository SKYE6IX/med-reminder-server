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
                                BigDecimal totalQuantity,
                                BigDecimal currentQuantity,
                                String notifyRule,
                                LocalDateTime addedAt,
                                MedicationProfileEntity medicationProfile) {
        this.id = id;
        this.totalQuantity = totalQuantity;
        this.currentQuantity = currentQuantity;
        this.notifyRule = notifyRule;
        this.addedAt = addedAt;
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

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public MedicationProfileEntity getMedicationProfile() {
        return medicationProfile;
    }

    public void updateMedicationPack (MedicationPack medicationPack) {
        this.totalQuantity = medicationPack.getTotalQuantity();
        this.currentQuantity = medicationPack.getCurrentQuantity();
        this.notifyRule = medicationPack.getNotifyRule();
        this.addedAt = medicationPack.getAddedAt();
    }
}
