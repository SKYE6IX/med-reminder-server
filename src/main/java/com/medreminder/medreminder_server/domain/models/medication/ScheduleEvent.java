package com.medreminder.medreminder_server.domain.models.medication;

import java.time.LocalDateTime;

public class ScheduleEvent {

    private final String id;
    private final double dosage;
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

    public void updateScheduleEvent(String status, boolean isTaken){
        this.status = Status.valueOf(status);
        if(isTaken){
            this.takenAt = LocalDateTime.now();
        }
    }
}
