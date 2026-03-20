package com.medreminder.medreminder_server.domain.models.medication;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MedicationSchedule {

    private String id;
    private double doseQuantity;
    private String recurrenceRule;
    private LocalDateTime startTime;
    private LocalDateTime startDate;
    private LocalDateTime lastExpandedUntil;
    private String timeZone;

    public MedicationSchedule(String id,
                              double doseQuantity,
                              String recurrenceRule,
                              LocalDateTime startTime,
                              LocalDateTime startDate,
                              String timeZone) {
        this.id = id;
        this.doseQuantity = doseQuantity;
        this.recurrenceRule = recurrenceRule;
        this.startTime = startTime;
        this.startDate = startDate;
        this.timeZone = timeZone;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getLastExpandedUntil() {
        return lastExpandedUntil;
    }

    public String getTimeZone() {
        return timeZone;
    }
}
