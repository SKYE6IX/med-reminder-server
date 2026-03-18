package com.medreminder.medreminder_server.infrastructure.entity.medications;

import jakarta.persistence.*;
import org.hibernate.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "MEDICATION_SCHEDULES")
public class MedicationScheduleEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    @Column(name = "dose_quantity")
    private double doseQuantity;

    @Column(name = "recurrence_rule")
    private String recurrenceRule;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "start_date")
    private LocalDate startDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_profile_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MedicationProfileEntity medicationProfile;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public MedicationScheduleEntity() {
    }

    public MedicationScheduleEntity(String id,
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

    public void addMedicationProfile(MedicationProfileEntity medicationProfile){
        this.medicationProfile = medicationProfile;
    }
}
