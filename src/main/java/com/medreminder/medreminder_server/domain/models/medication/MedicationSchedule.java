package com.medreminder.medreminder_server.domain.models.medication;

import com.medreminder.medreminder_server.infrastructure.entity.medications.ScheduleEventEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedicationSchedule {

    private final String id;
    private BigDecimal doseQuantity;
    private BigDecimal takenQuantity;
    private String recurrenceRule;
    private LocalDateTime startTime;
    private final LocalDate startDate;
    private LocalDateTime lastExpandedUntil;
    private final String timeZone;
    private MedicationProfile medicationProfile;
    private final List<ScheduleEvent> scheduleEvents = new ArrayList<>();

    public MedicationSchedule(String id,
                              BigDecimal doseQuantity,
                              String recurrenceRule,
                              LocalDate startDate,
                              String timeZone,
                              BigDecimal takenQuantity,
                              LocalDateTime lastExpandedUntil) {
        this.id = id;
        this.doseQuantity = doseQuantity;
        this.recurrenceRule = recurrenceRule;
        this.startDate = startDate;
        this.timeZone = timeZone;
        this.takenQuantity = takenQuantity;
        this.lastExpandedUntil = lastExpandedUntil;
    }


    public String getId() {
        return id;
    }

    public BigDecimal getDoseQuantity() {
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

    public BigDecimal getTakenQuantity() {
        return takenQuantity;
    }

    public void updateDoseQuantity(BigDecimal  doseQuantity) {
        this.doseQuantity = doseQuantity;
    }

    public void updateTakenQuantity(BigDecimal takenQuantity) {
        this.takenQuantity = takenQuantity;
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

    public void  updateLastExpandedUntil(LocalDateTime lastExpandedUntil) {
        this.lastExpandedUntil = lastExpandedUntil;
    }
}
