package com.medreminder.medreminder_server.domain.models.medication;

import com.medreminder.medreminder_server.domain.models.users.Profile;

import java.time.LocalDateTime;

public class MedicationProfile {

    private String id;
    private boolean isActive;
    private LocalDateTime startAt;
    private String note;

    private Profile  profile;
    private Medication medication;

    public MedicationProfile(String id,
                             boolean isActive,
                             LocalDateTime startAt,
                             String note,
                             Profile profile,
                             Medication medication) {
        this.id = id;
        this.isActive = isActive;
        this.startAt = startAt;
        this.note = note;
        this.profile = profile;
        this.medication = medication;
    }

    public String getId() {
        return id;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDateTime getStartTime() {
        return startAt;
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
}
