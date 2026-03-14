package com.medreminder.medreminder_server.infrastructure.entity.medications;

import jakarta.persistence.*;
import org.hibernate.annotations.*;

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
}
