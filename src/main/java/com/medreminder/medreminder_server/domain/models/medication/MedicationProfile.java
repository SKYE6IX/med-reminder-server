package com.medreminder.medreminder_server.domain.models.medication;

import com.medreminder.medreminder_server.domain.models.users.Profile;

import java.util.Objects;

public class MedicationProfile {

    private final String id;

    private boolean isActive;

    private String note;

    private Profile  profile;

    private Medication medication;

    private MedicationSchedule medicationSchedule;

    private MedicationPack medicationPack;

    public MedicationProfile(String id,
                             boolean isActive,
                             String note) {
        this.id = id;
        this.isActive = isActive;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getNote() {
        return note;
    }

    public Profile getProfile() {
        return profile;
    }

    public Medication getMedication() {
        return medication;
    }

    public MedicationSchedule getMedicationSchedule() {
        return medicationSchedule;
    }

    public MedicationPack getMedicationPack() {
        return medicationPack;
    }

    public void updateActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void updateNote(String note) {
        this.note = note;
    }

    public void addProfile(Profile profile) {
        this.profile = profile;
    }

    public void addMedication(Medication medication) {
        this.medication = medication;
        medication.addMedicationProfile(this);
    }

    public void addMedicationSchedule(MedicationSchedule medicationSchedule) {
        this.medicationSchedule = medicationSchedule;
        medicationSchedule.addMedicationProfile(this);
    }

    public void addMedicationPack(MedicationPack medicationPack) {
        if (medicationPack != null) {
            this.medicationPack = medicationPack;
            medicationPack.addMedicationProfile(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }

        if(obj == null || getClass() != obj.getClass()){
            return false;
        }

        MedicationProfile that = (MedicationProfile) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
