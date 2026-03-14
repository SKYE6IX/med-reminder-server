package com.medreminder.medreminder_server.infrastructure.entity.medications;


import com.medreminder.medreminder_server.domain.models.medication.Medication;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import org.hibernate.annotations.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;

    @OneToOne(
            mappedBy = "medicationProfile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private MedicationEntity medication;

    @OneToOne(
            mappedBy = "medicationProfile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private MedicationPackEntity medicationPack;

    @OneToOne(
            mappedBy = "medicationProfile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private MedicationScheduleEntity medicationSchedule;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public MedicationProfileEntity() {
    }

    public MedicationProfileEntity(String id,
                                   boolean isActive,
                                   LocalDateTime startAt, String note) {
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
