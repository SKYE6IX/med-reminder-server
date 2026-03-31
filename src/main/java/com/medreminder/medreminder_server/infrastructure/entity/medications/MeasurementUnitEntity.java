package com.medreminder.medreminder_server.infrastructure.entity.medications;

import jakarta.persistence.*;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;


@Entity(name = "MEASUREMENT_UNITS")
public class MeasurementUnitEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    private String name;

    private String symbol;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MedicationEntity medication;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public MeasurementUnitEntity() {
    }

    public MeasurementUnitEntity(String id,
                                 String name,
                                 String symbol,
                                 MedicationEntity medication) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.medication = medication;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public MedicationEntity getMedication() {
        return medication;
    }
}
