package com.medreminder.medreminder_server.domain.models.medication;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MedicationSchedule {

    private String id;
    private double doseQuantity;
    private String recurrenceRule;
    private LocalDateTime startTime;
    private LocalDate startDate;



    public MedicationSchedule(String id,
                              double doseQuantity,
                              String recurrenceRule,
                              LocalDateTime startTime,
                              LocalDate startDate) {
        this.id = id;
        this.doseQuantity = doseQuantity;
        this.recurrenceRule = recurrenceRule;
        this.startTime = startTime;
        this.startDate = startDate;
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

    public LocalDate getStartDate() {
        return startDate;
    }
}
