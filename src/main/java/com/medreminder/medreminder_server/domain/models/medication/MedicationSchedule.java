package com.medreminder.medreminder_server.domain.models.medication;

import java.time.LocalDateTime;

public class MedicationSchedule {

    private String id;
    private double doseQuantity;
    private String recurrenceRule;
    private LocalDateTime startAt;

    private MedicationProfile medicationProfile;

    public MedicationSchedule(String id,
                              double doseQuantity,
                              String recurrenceRule,
                              LocalDateTime startAt,
                              MedicationProfile medicationProfile) {
        this.id = id;
        this.doseQuantity = doseQuantity;
        this.recurrenceRule = recurrenceRule;
        this.startAt = startAt;
        this.medicationProfile = medicationProfile;
    }

    public String getId() {
        return id;
    }

    public double getDoseQuantity() {
        return doseQuantity;
    }

    public String getRecurrenceRule() {
        return recurrenceRule;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public MedicationProfile getMedicationProfile() {
        return medicationProfile;
    }
}
