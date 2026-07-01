package com.medreminder.medreminder_server.domain.models.medication;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MedicationPack {

    private final String id;
    private final BigDecimal totalQuantity;
    private BigDecimal currentQuantity;
    private final int reminderDays;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private MedicationPackStatus status;
    private MedicationProfile medicationProfile;
    private boolean isRefilled;

    public MedicationPack(String id,
                          BigDecimal totalQuantity,
                          BigDecimal currentQuantity,
                          int reminderDays,
                          LocalDateTime startedAt,
                          LocalDateTime endedAt,
                          MedicationPackStatus status,
                          boolean isRefilled) {
        this.id = id;
        this.totalQuantity = totalQuantity;
        this.currentQuantity = currentQuantity;
        this.reminderDays = reminderDays;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.status = status;
        this.isRefilled = isRefilled;
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

    public int getReminderDays() {
        return reminderDays;
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

    public boolean isRefilled() {
        return isRefilled;
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

    public void updateIsRefilled(boolean isRefilled) {
        this.isRefilled = isRefilled;
    }

    public void addMedicationProfile(MedicationProfile medicationProfile) {
        this.medicationProfile = medicationProfile;
    }
}
