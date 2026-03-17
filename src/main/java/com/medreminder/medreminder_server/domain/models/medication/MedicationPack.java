package com.medreminder.medreminder_server.domain.models.medication;

import java.time.LocalDateTime;

public class MedicationPack {

    private String id;
    private double totalQuantity;
    private double currentQuantity;
    private String notifyRule;
    private LocalDateTime addedAt;

    public MedicationPack(String id,
                          double totalQuantity,
                          String notifyRule,
                          LocalDateTime addedAt) {
        this.id = id;
        this.totalQuantity = totalQuantity;
        this.notifyRule = notifyRule;
        this.addedAt = addedAt;
    }

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
}
