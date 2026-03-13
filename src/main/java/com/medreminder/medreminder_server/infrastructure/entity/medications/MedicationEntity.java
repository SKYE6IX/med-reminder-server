package com.medreminder.medreminder_server.infrastructure.entity.medications;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.annotations.UuidGenerator;

@Entity(name = "MEDICATIONS")
public class MedicationEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    private String name;

    @Column(name = "unit_type")
    private String unitType;

    public MedicationEntity() {
    }

    public MedicationEntity(String name, String unitType) {
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
}
