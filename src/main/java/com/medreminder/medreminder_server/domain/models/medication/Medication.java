package com.medreminder.medreminder_server.domain.models.medication;

public class Medication {

    private final String  id;
    private final String name;
    private final Unit unitType;
    private final MeasurementUnit measurementUnit;

    public Medication(String id,
                      String name,
                      Unit unitType,
                      MeasurementUnit measurementUnit) {
        this.id = id;
        this.name = name;
        this.unitType = unitType;
        this.measurementUnit = measurementUnit;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Unit getUnitType() {
        return unitType;
    }

    public MeasurementUnit getMeasurementUnit() {
        return measurementUnit;
    }

}
