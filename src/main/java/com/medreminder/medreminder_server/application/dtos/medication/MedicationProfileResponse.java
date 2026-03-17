package com.medreminder.medreminder_server.application.dtos.medication;

import com.medreminder.medreminder_server.application.dtos.user.ProfileResponse;

public class MedicationProfileResponse {

    private String id;
    private String medicationName;
    private String medicationUnit;
    private String status;
    private String note;
    private String createdAt;
    private ProfileResponse profile;
    private MedScheduleResponse schedule;


    public MedicationProfileResponse() {
    }

    public MedicationProfileResponse(String id,
                                     String medicationName,
                                     String medicationUnit, String status,
                                     String note, String createdAt) {
        this.id = id;
        this.medicationName = medicationName;
        this.medicationUnit = medicationUnit;
        this.status = status;
        this.note = note;
        this.createdAt = createdAt;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public ProfileResponse getProfile() {
        return profile;
    }

    public MedScheduleResponse getSchedule() {
        return schedule;
    }

    public void setProfile(ProfileResponse profile) {
        this.profile = profile;
    }

    public void setSchedule(MedScheduleResponse schedule) {
        this.schedule = schedule;
    }
}