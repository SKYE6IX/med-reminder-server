package com.medreminder.medreminder_server.domain.models.medication;

import java.time.LocalDateTime;

public class MedicationSchedule {

    private String id;
    private double doseQuantity;
    private String recurrenceRule;
    private LocalDateTime startAt;

    public MedicationSchedule(String id,
                              double doseQuantity,
                              String recurrenceRule,
                              LocalDateTime startAt) {
        this.id = id;
        this.doseQuantity = doseQuantity;
        this.recurrenceRule = recurrenceRule;
        this.startAt = startAt;
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
}
