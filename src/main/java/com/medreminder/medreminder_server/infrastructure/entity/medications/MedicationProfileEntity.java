package com.medreminder.medreminder_server.infrastructure.entity.medications;


import com.medreminder.medreminder_server.domain.models.medication.MedicationProfile;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity(name = "MEDICATION_PROFILES")
public class MedicationProfileEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    @Column(name = "is_active")
    private boolean isActive;

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
    private MedicationScheduleEntity medicationSchedule;

    @OneToOne(
            mappedBy = "medicationProfile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private MedicationPackEntity medicationPack;

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
                                   String note,
                                   ProfileEntity profile) {
        this.id = id;
        this.isActive = isActive;
        this.note = note;
        this.profile = profile;
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

    public MedicationEntity getMedication() { return medication; }

    public MedicationScheduleEntity getMedicationSchedule() {
        return medicationSchedule;
    }

    public MedicationPackEntity getMedicationPack() {
        return medicationPack;
    }

    public ProfileEntity getProfile() {
        return profile;
    }

    public Optional<LocalDateTime> getCreatedAt() {
       if (createdAt == null) {
           return Optional.empty();
       }
       return Optional.of(createdAt);
    }

    public void updateMedicationProfile(MedicationProfile medicationProfile) {
        this.isActive = medicationProfile.isActive();
        this.note = medicationProfile.getNote();
    }

    public void setMedication(MedicationEntity medication) {
        this.medication = medication;
    }

    public void setMedicationSchedule(MedicationScheduleEntity medicationSchedule) {
        this.medicationSchedule = medicationSchedule;
    }

    public void setMedicationPack(MedicationPackEntity medicationPack) {
        this.medicationPack = medicationPack;
    }
}
