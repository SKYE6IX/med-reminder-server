package com.medreminder.medreminder_server.domain.models.medication;

import java.time.LocalDateTime;

public class MedicationPack {

    private String id;
    private double totalQuantity;
    private double currentQuantity;
    private double totalAmount;
    private LocalDateTime addedAt;

    private MedicationProfile medicationProfile;


    public MedicationPack(String id, double totalQuantity,
                          double currentQuantity,
                          double totalAmount,
                          LocalDateTime addedAt,
                          MedicationProfile medicationProfile) {
        this.id = id;
        this.totalQuantity = totalQuantity;
        this.currentQuantity = currentQuantity;
        this.totalAmount = totalAmount;
        this.addedAt = addedAt;
        this.medicationProfile = medicationProfile;
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

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public MedicationProfile getMedicationProfile() {
        return medicationProfile;
    }
}
