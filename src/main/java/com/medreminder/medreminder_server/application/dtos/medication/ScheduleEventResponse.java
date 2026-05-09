package com.medreminder.medreminder_server.application.dtos.medication;

import com.medreminder.medreminder_server.application.dtos.user.ProfileResponse;

public class ScheduleEventResponse {

    private final String id;
    private final String status;
    private final String medicationName;
    private final String medicationImageUrl;
    private final String dosage;
    private final String measurement;
    private final String scheduleAt;
    private String takenAt;
    private ProfileResponse profile;

    public ScheduleEventResponse(String id,
                                 String status,
                                 String medicationName,
                                 String medicationImageUrl,
                                 String dosage,
                                 String measurement,
                                 String scheduleAt) {
        this.id = id;
        this.status = status;
        this.medicationName = medicationName;
        this.medicationImageUrl = medicationImageUrl;
        this.dosage = dosage;
        this.measurement = measurement;
        this.scheduleAt = scheduleAt;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public String getMeasurement() {
        return measurement;
    }

    public String getDosage() {
        return dosage;
    }

    public String getMedicationImageUrl() {
        return medicationImageUrl;
    }

    public String getScheduleAt() {
        return scheduleAt;
    }

    public String getTakenAt() {
        return takenAt;
    }

    public ProfileResponse getProfile() {
        return profile;
    }

    public void setProfile(ProfileResponse profile) {
        this.profile = profile;
    }

    public void setTakenAt(String takenAt) {
        this.takenAt = takenAt;
    }
}
