package com.medreminder.medreminder_server.application.dtos.medication;

import com.medreminder.medreminder_server.application.dtos.user.ProfileResponse;

public class MedicationProfileResponse {

    private String id;
    private String medicationName;
    private String medicationUnit;
    private String status;
    private String note;
    private String amountTaken;
    private String amountInPack;
    private ProfileResponse profile;
    private MedScheduleResponse schedule;


    public MedicationProfileResponse() {
    }

    public MedicationProfileResponse(String id,
                                     String medicationName,
                                     String medicationUnit, String status,
                                     String note) {
        this.id = id;
        this.medicationName = medicationName;
        this.medicationUnit = medicationUnit;
        this.status = status;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public String getMedicationUnit() {
        return medicationUnit;
    }

    public String getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }

    public ProfileResponse getProfile() {
        return profile;
    }

    public MedScheduleResponse getSchedule() {
        return schedule;
    }

    public String getAmountTaken() {
        return amountTaken;
    }

    public String getAmountInPack() {
        return amountInPack;
    }

    public void setProfile(ProfileResponse profile) {
        this.profile = profile;
    }

    public void setSchedule(MedScheduleResponse schedule) {
        this.schedule = schedule;
    }

    public void setAmountTaken(String amountTaken) {
        this.amountTaken = amountTaken;
    }

    public void setAmountInPack(String amountInPack) {
        this.amountInPack = amountInPack;
    }
}