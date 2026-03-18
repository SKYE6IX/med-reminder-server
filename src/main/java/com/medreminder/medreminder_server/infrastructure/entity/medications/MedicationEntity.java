package com.medreminder.medreminder_server.infrastructure.entity.medications;


import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
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

    @OneToOne(
            mappedBy = "medication",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private MeasurementUnitEntity measurementUnit;

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
                            String unitType) {
        this.id = id;
        this.name = name;
        this.unitType = unitType;
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

    public MeasurementUnitEntity getMeasurementUnit() {
        return measurementUnit;
    }

    public void addMedicationProfile(MedicationProfileEntity medicationProfile){
        this.medicationProfile = medicationProfile;
    }

    public void addMeasurementUnit(MeasurementUnitEntity measurementUnit){
        this.measurementUnit = measurementUnit;
        measurementUnit.addMedication(this);
    }
}
