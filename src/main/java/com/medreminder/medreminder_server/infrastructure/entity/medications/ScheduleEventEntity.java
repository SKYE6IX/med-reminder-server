package com.medreminder.medreminder_server.infrastructure.entity.medications;

import com.medreminder.medreminder_server.domain.models.medication.ScheduleEvent;
import jakarta.persistence.*;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Entity(name = "SCHEDULE_EVENTS")
public class ScheduleEventEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    private double dosage;

    private String status;

    @Column(name = "schedule_at")
    private LocalDateTime scheduleAt;

    @Column(name = "taken_at")
    private LocalDateTime takenAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_schedule_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MedicationScheduleEntity medicationSchedule;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public ScheduleEventEntity() {
    }

    public ScheduleEventEntity(String id,
                               double dosage,
                               String status,
                               LocalDateTime scheduleAt,
                               MedicationScheduleEntity medicationSchedule){
        this.id = id;
        this.dosage = dosage;
        this.status = status;
        this.scheduleAt = scheduleAt;
        this.medicationSchedule = medicationSchedule;
    }

    public String getId() {
        return id;
    }

    public double getDosage() {
        return dosage;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getScheduleAt() {
        return scheduleAt;
    }

    public LocalDateTime getTakenAt() {
        return takenAt;
    }

    public MedicationScheduleEntity getMedicationSchedule() {
        return medicationSchedule;
    }

    public void updateScheduleEvent(ScheduleEvent scheduleEvent) {
        this.dosage = scheduleEvent.getDosage();
        this.status = scheduleEvent.getStatus();
        this.scheduleAt = scheduleEvent.getScheduleAt();
        this.takenAt = scheduleEvent.getTakenAt();
    }
}