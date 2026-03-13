package com.medreminder.medreminder_server.infrastructure.entity.medications;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.annotations.UuidGenerator;


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
