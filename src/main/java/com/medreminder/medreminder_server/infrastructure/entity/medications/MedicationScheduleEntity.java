package com.medreminder.medreminder_server.infrastructure.entity.medications;

import com.medreminder.medreminder_server.domain.models.medication.MedicationSchedule;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import org.hibernate.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "MEDICATION_SCHEDULES")
public class MedicationScheduleEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    @Column(name = "dose_quantity")
    private BigDecimal doseQuantity;

    @Column(name = "taken_quantity")
    private BigDecimal takenQuantity;

    @Column(name = "recurrence_rule")
    private String recurrenceRule;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "last_expanded_until")
    private LocalDateTime lastExpandedUntil;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_profile_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MedicationProfileEntity medicationProfile;

    @OneToMany(
            mappedBy = "medicationSchedule",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<ScheduleEventEntity > scheduleEvents = new ArrayList<>();

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public MedicationScheduleEntity() {
    }

    public MedicationScheduleEntity(String id,
                                    BigDecimal doseQuantity,
                                    BigDecimal takenQuantity,
                                    String recurrenceRule,
                                    LocalDateTime startTime,
                                    LocalDate startDate,
                                    LocalDate endDate,
                                    LocalDateTime lastExpandedUntil,
                                    MedicationProfileEntity medicationProfile
                                    ) {
        this.id = id;
        this.doseQuantity = doseQuantity;
        this.takenQuantity = takenQuantity;
        this.recurrenceRule = recurrenceRule;
        this.startTime = startTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lastExpandedUntil = lastExpandedUntil;
        this.medicationProfile = medicationProfile;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getDoseQuantity() {
        return doseQuantity;
    }

    public BigDecimal getTakenQuantity() {
        return takenQuantity;
    }

    public String getRecurrenceRule() {
        return recurrenceRule;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDateTime getLastExpandedUntil() {
        return lastExpandedUntil;
    }

    public MedicationProfileEntity getMedicationProfile() {
        return medicationProfile;
    }

    public List<ScheduleEventEntity> getScheduleEvents() {
        return scheduleEvents;
    }

    public void updateMedicationSchedule(MedicationSchedule domainMedicationSchedule){
        this.doseQuantity = domainMedicationSchedule.getDoseQuantity();
        this.takenQuantity = domainMedicationSchedule.getTakenQuantity();
        this.recurrenceRule = domainMedicationSchedule.getRecurrenceRule();
        this.startTime = domainMedicationSchedule.getStartTime();
        this.lastExpandedUntil = domainMedicationSchedule.getLastExpandedUntil();
    }
}