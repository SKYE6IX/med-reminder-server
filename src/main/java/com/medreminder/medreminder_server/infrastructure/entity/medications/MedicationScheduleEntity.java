package com.medreminder.medreminder_server.infrastructure.entity.medications;

import com.medreminder.medreminder_server.domain.models.medication.MedicationSchedule;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import org.hibernate.annotations.*;

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
    private double doseQuantity;

    @Column(name = "recurrence_rule")
    private String recurrenceRule;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "last_expanded_until")
    private LocalDateTime lastExpandedUntil;

    @Column(name = "time_zone")
    private String timeZone;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_profile_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MedicationProfileEntity medicationProfile;

    @OneToMany(
            mappedBy = "medicationSchedule",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Fetch(FetchMode.JOIN)
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
                                    double doseQuantity,
                                    String recurrenceRule,
                                    LocalDateTime startDate,
                                    String timeZone) {
        this.id = id;
        this.doseQuantity = doseQuantity;
        this.recurrenceRule = recurrenceRule;
        this.startDate = startDate;
        this.timeZone = timeZone;
    }

    public String getId() {
        return id;
    }

    public double getDoseQuantity() {
        return doseQuantity;
    }

    public String getRecurrenceRule() {
        return recurrenceRule;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getLastExpandedUntil() {
        return lastExpandedUntil;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public MedicationProfileEntity getMedicationProfile() {
        return medicationProfile;
    }

    public List<ScheduleEventEntity> getScheduleEvents() {
        return scheduleEvents;
    }

    public void addMedicationProfile(MedicationProfileEntity medicationProfile){
        this.medicationProfile = medicationProfile;
    }

    public void addStartTime (LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void addScheduleEvent(ScheduleEventEntity scheduleEvent){
        this.scheduleEvents.add(scheduleEvent);
        scheduleEvent.addMedicationSchedule(this);
    }

    public void updateMedicationSchedule(MedicationSchedule medicationSchedule){
        this.doseQuantity = medicationSchedule.getDoseQuantity();
        this.recurrenceRule = medicationSchedule.getRecurrenceRule();
    }
}
