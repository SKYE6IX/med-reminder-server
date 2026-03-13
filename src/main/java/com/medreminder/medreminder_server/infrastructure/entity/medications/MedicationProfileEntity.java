package com.medreminder.medreminder_server.infrastructure.entity.medications;


import com.medreminder.medreminder_server.domain.models.medication.Medication;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity(name = "MEDICATION_PROFILES")
public class MedicationProfileEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    private String note;

//    private ProfileEntity profile;

//    private MedicationEntity medication;


    public MedicationProfileEntity() {
    }

    public MedicationProfileEntity(String id, boolean isActive, LocalDateTime startAt, String note) {
        this.id = id;
        this.isActive = isActive;
        this.startAt = startAt;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public String getNote() {
        return note;
    }
}
