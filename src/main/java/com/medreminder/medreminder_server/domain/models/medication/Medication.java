package com.medreminder.medreminder_server.domain.models.medication;

public class Medication {

    private final String  id;
    private final String name;
    private final Unit unitType;
    private MeasurementUnit measurementUnit;
    private MedicationProfile medicationProfile;

    public Medication(String id,
                      String name,
                      Unit unitType) {
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

    public Unit getUnitType() {
        return unitType;
    }

    public MeasurementUnit getMeasurementUnit() {
        return measurementUnit;
    }

    public MedicationProfile getMedicationProfile() {
        return medicationProfile;
    }

    public void addMeasurementUnit(MeasurementUnit measurementUnit){
        this.measurementUnit = measurementUnit;
        measurementUnit.addMedication(this);
    }

    public void addMedicationProfile(MedicationProfile medicationProfile){
        this.medicationProfile = medicationProfile;
    }
}
