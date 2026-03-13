package com.medreminder.medreminder_server.infrastructure.entity.medications;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.annotations.UuidGenerator;

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

    @Column(name = "start_at")
    private LocalDateTime startAt;

//    private MedicationProfile medicationProfile;
}
