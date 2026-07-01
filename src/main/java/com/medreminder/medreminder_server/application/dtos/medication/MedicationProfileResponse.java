package com.medreminder.medreminder_server.application.dtos.medication;

import com.medreminder.medreminder_server.application.dtos.user.ProfileResponse;

public class MedicationProfileResponse {

    public record Pack(String totalAmountInPack, String currentAmountInPack) { }

    private String id;
    private String medicationName;
    private String medicationUnit;
    private String status;
    private String note;
    private String medicationReason;
    private Pack pack;
    private ProfileResponse profile;
    private MedScheduleResponse schedule;

    public MedicationProfileResponse() {
    }

    public MedicationProfileResponse(String id,
                                     String medicationName,
                                     String medicationUnit,
                                     String status,
                                     String note,
                                     String medicationReason) {
        this.id = id;
        this.medicationName = medicationName;
        this.medicationUnit = medicationUnit;
        this.status = status;
        this.note = note;
        this.medicationReason = medicationReason;
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

    public String getMedicationReason() {
        return medicationReason;
    }

    public ProfileResponse getProfile() {
        return profile;
    }

    public MedScheduleResponse getSchedule() {
        return schedule;
    }

    public Pack getPack() {
        return pack;
    }

    public void setProfile(ProfileResponse profile) {
        this.profile = profile;
    }

    public void setSchedule(MedScheduleResponse schedule) {
        this.schedule = schedule;
    }

    public void setPack(String totalAmountInPack, String currentAmountInPack) {
        this.pack = new Pack(totalAmountInPack, currentAmountInPack);
    }
}