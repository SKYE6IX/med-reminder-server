package com.medreminder.medreminder_server.infrastructure.entity.subscription;

import com.medreminder.medreminder_server.domain.models.subscription.Plan;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Entity(name = "PLANS")
public class PlanEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    @Column(name = "plan_type")
    private String planType;

    @Column(name = "max_medications")
    private int maxMedications;

    @Column(name = "managed_relation")
    private boolean managedRelation;

    @Column(name = "refill_reminders")
    private boolean refillReminders;

    @Column(name = "reminder_preference")
    private boolean reminderPreference;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;

    @OneToOne(
            mappedBy = "plan",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private SubscriptionEntity subscription;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public PlanEntity() {
    }

    public PlanEntity(String id,
                      String planType,
                      int maxMedications,
                      boolean managedRelation,
                      boolean refillReminders,
                      boolean reminderPreference,
                      UserEntity user) {
        this.id = id;
        this.planType = planType;
        this.maxMedications = maxMedications;
        this.managedRelation = managedRelation;
        this.refillReminders = refillReminders;
        this.reminderPreference = reminderPreference;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public String getPlanType() {
        return planType;
    }

    public int getMaxMedications() {
        return maxMedications;
    }

    public boolean isManagedRelation() {
        return managedRelation;
    }

    public boolean isRefillReminders() {
        return refillReminders;
    }

    public boolean isReminderPreference() {
        return reminderPreference;
    }

    public UserEntity getUser() {
        return user;
    }

    public void syncPlanData(Plan domainPlan) {
        this.planType = domainPlan.getPlanType().toString();
        this.maxMedications = domainPlan.getMaxMedications();
        this.managedRelation = domainPlan.isManagedRelation();
        this.refillReminders = domainPlan.isRefillReminders();
        this.reminderPreference = domainPlan.isReminderPreference();
    }
}
