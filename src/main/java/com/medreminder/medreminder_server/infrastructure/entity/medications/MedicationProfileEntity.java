package com.medreminder.medreminder_server.infrastructure.entity.medications;


import com.medreminder.medreminder_server.domain.models.medication.MedicationProfile;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import org.hibernate.annotations.*;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity(name = "MEDICATION_PROFILES")
public class MedicationProfileEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    @Column(name = "is_active")
    private boolean isActive;

    @JdbcTypeCode(Types.LONGVARCHAR)
    private String note;

    @Column(name = "medication_reason")
    private String medicationReason;

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

    @OneToMany(
            mappedBy = "medicationProfile",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<MedicationPackEntity> medicationPacks = new ArrayList<>();

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
                                   String medicationReason,
                                   ProfileEntity profile) {
        this.id = id;
        this.isActive = isActive;
        this.note = note;
        this.medicationReason = medicationReason;
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

    public String getMedicationReason() {
        return medicationReason;
    }

    public MedicationEntity getMedication() { return medication; }

    public MedicationScheduleEntity getMedicationSchedule() {
        return medicationSchedule;
    }

    public List<MedicationPackEntity> getMedicationPacks() {
        return medicationPacks;
    }

    public ProfileEntity getProfile() {
        return profile;
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
}
