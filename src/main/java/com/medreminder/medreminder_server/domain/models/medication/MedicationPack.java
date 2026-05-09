package com.medreminder.medreminder_server.domain.models.medication;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MedicationPack {

    private final String id;
    private final BigDecimal totalQuantity;
    private BigDecimal currentQuantity;
    private final String notifyRule;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private MedicationPackStatus status;
    private MedicationProfile medicationProfile;

    public MedicationPack(String id,
                          BigDecimal totalQuantity,
                          BigDecimal currentQuantity,
                          String notifyRule,
                          LocalDateTime startedAt,
                          LocalDateTime endedAt,
                          MedicationPackStatus status
                          ) {
        this.id = id;
        this.totalQuantity = totalQuantity;
        this.currentQuantity = currentQuantity;
        this.notifyRule = notifyRule;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.status = status;
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

    public String getNotifyRule() {
        return notifyRule;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public MedicationPackStatus getStatus() {
        return status;
    }

    public MedicationProfile getMedicationProfile() {
        return medicationProfile;
    }

    public void updateCurrentQuantity(BigDecimal currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public void updateStartedAt(LocalDateTime startedAt) {
      this.startedAt = startedAt;
    }

    public void updateEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public void updateStatus(MedicationPackStatus status) {
        this.status = status;
    }

    public void addMedicationProfile(MedicationProfile medicationProfile) {
        this.medicationProfile = medicationProfile;
    }
}
