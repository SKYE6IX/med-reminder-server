package com.medreminder.medreminder_server.domain.models.medication;

import com.medreminder.medreminder_server.infrastructure.entity.medications.ScheduleEventEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedicationSchedule {

    private final String id;
    private double doseQuantity;
    private String recurrenceRule;
    private LocalDateTime startTime;
    private final LocalDate startDate;
    private LocalDateTime lastExpandedUntil;
    private final String timeZone;
    private MedicationProfile medicationProfile;
    private final List<ScheduleEvent> scheduleEvents = new ArrayList<>();

    public MedicationSchedule(String id,
                              double doseQuantity,
                              String recurrenceRule,
                              LocalDate startDate,
                              String timeZone) {
        this.id = id;
        this.doseQuantity = doseQuantity;
        this.recurrenceRule = recurrenceRule;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDateTime getLastExpandedUntil() {
        return lastExpandedUntil;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public MedicationProfile getMedicationProfile() {
        return medicationProfile;
    }

    public List<ScheduleEvent> getScheduleEvents() {
        return scheduleEvents;
    }

    public void updateDoseQuantity(double doseQuantity) {
        this.doseQuantity = doseQuantity;
    }

    public void updateRecurrenceRule(String recurrenceRule) {
        this.recurrenceRule = recurrenceRule;
    }

    public void updateStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void addScheduleEvent(ScheduleEvent scheduleEvent) {
        this.scheduleEvents.add(scheduleEvent);
        scheduleEvent.setMedicationSchedule(this);
    }

    public void addMedicationProfile(MedicationProfile medicationProfile) {
        this.medicationProfile = medicationProfile;
    }
}
