package com.medreminder.medreminder_server.domain.models.medication;

public class Medication {

    private final String  id;
    private final String name;
    private final Unit unitType;
    private final Measurement measurement;
    private MedicationProfile medicationProfile;

    public Medication(String id,
                      String name,
                      Unit unitType, Measurement measurement) {
        this.id = id;
        this.name = name;
        this.unitType = unitType;
        this.measurement = measurement;
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

    public Measurement getMeasurement() {
        return measurement;
    }

    public MedicationProfile getMedicationProfile() {
        return medicationProfile;
    }

    public void addMedicationProfile(MedicationProfile medicationProfile){
        this.medicationProfile = medicationProfile;
    }
}
