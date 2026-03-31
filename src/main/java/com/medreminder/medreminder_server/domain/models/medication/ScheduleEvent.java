package com.medreminder.medreminder_server.domain.models.medication;

import java.time.LocalDateTime;
import java.util.Objects;

public class ScheduleEvent {

    private final String id;
    private double dosage;
    private Status status;
    private final LocalDateTime scheduleAt;
    private LocalDateTime takenAt;
    private MedicationSchedule medicationSchedule;

    public ScheduleEvent(String id,
                         double dosage,
                         LocalDateTime scheduleAt) {
        this.id = id;
        this.dosage = dosage;
        this.status = Status.PENDING;
        this.scheduleAt = scheduleAt;
    }

    public ScheduleEvent(String id,
                         double dosage,
                         String status,
                         LocalDateTime scheduleAt,
                         LocalDateTime takenAt) {
        this.id = id;
        this.dosage = dosage;
        this.status = Status.valueOf(status);
        this.scheduleAt = scheduleAt;
        this.takenAt = takenAt;
    }

    static enum Status {
        PENDING,
        TAKEN,
        MISSED
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status.name();
    }

    public LocalDateTime getScheduleAt() {
        return scheduleAt;
    }

    public LocalDateTime getTakenAt() {
        return takenAt;
    }

    public double getDosage() {
        return dosage;
    }

    public MedicationSchedule getMedicationSchedule() {
        return medicationSchedule;
    }

    public void setMedicationSchedule(MedicationSchedule medicationSchedule) {
        this.medicationSchedule = medicationSchedule;
    }

    public void updateDosage(double dosage) {
        this.dosage = dosage;
    }

    public void updateStatus(String status) {
        this.status = Status.valueOf(status.toUpperCase());
    }

    public void updateTakenAt(LocalDateTime takenAt) {
        this.takenAt = takenAt;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) {
            return true;
        }

        if ( obj == null || getClass() != obj.getClass() ) {
            return false;
        }

        ScheduleEvent other = (ScheduleEvent) obj;
        return Objects.equals(id, other.id);
    }
}
