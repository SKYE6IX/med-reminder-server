package com.medreminder.medreminder_server.infrastructure.entity.medications;


import jakarta.persistence.*;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Entity(name = "MEDICATIONS")
public class MedicationEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    private String name;

    @Column(name = "unit_type")
    private String unitType;

    private String measurement;

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

    public MedicationEntity() {
    }

    public MedicationEntity(String id,
                            String name,
                            String unitType,
                            String measurement,
                            MedicationProfileEntity medicationProfile) {
        this.id = id;
        this.name = name;
        this.unitType = unitType;
        this.measurement = measurement;
        this.medicationProfile = medicationProfile;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUnitType() {
        return unitType;
    }

    public String getMeasurement() {
        return measurement;
    }

    public MedicationProfileEntity getMedicationProfile() {
        return medicationProfile;
    }
}
