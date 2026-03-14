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

    @Column(name = "is_liquid")
    private boolean isLiquid;

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
                                 boolean isLiquid) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.isLiquid = isLiquid;
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

    public boolean isLiquid() {
        return isLiquid;
    }
}
