package com.medreminder.medreminder_server.domain.models.medication;

import java.time.LocalDateTime;

public class MedicationPack {

    private final String id;
    private double totalQuantity;
    private double currentQuantity;
    private String notifyRule;
    private final LocalDateTime addedAt;
    private MedicationProfile medicationProfile;

    public MedicationPack(String id,
                          double totalQuantity,
                          double currentQuantity,
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

    public MedicationProfile getMedicationProfile() {
        return medicationProfile;
    }

    public void updateTotalQuantity(double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void updateCurrentQuantity(double currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public void updateNotifyRule(String notifyRule) {
        this.notifyRule = notifyRule;
    }

    public void addMedicationProfile(MedicationProfile medicationProfile) {
        this.medicationProfile = medicationProfile;
    }
}
