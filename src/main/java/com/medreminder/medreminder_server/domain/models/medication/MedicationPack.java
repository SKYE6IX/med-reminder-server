package com.medreminder.medreminder_server.domain.models.medication;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MedicationPack {

    private final String id;
    private BigDecimal totalQuantity;
    private BigDecimal  currentQuantity;
    private String notifyRule;
    private final LocalDateTime addedAt;
    private MedicationProfile medicationProfile;

    public MedicationPack(String id,
                          BigDecimal totalQuantity,
                          BigDecimal currentQuantity,
                          String notifyRule,
                          LocalDateTime addedAt) {
        this.id = id;
        this.totalQuantity = totalQuantity;
        this.currentQuantity = currentQuantity;
        this.notifyRule = notifyRule;
        this.addedAt = addedAt;
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

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public MedicationProfile getMedicationProfile() {
        return medicationProfile;
    }

    public void updateTotalQuantity(BigDecimal totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void updateCurrentQuantity(BigDecimal currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public void updateNotifyRule(String notifyRule) {
        this.notifyRule = notifyRule;
    }

    public void addMedicationProfile(MedicationProfile medicationProfile) {
        this.medicationProfile = medicationProfile;
    }
}
