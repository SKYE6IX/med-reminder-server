package com.medreminder.medreminder_server.domain.models.billing;

import com.medreminder.medreminder_server.domain.models.users.User;

public class Plan {

    private final String id;
    private PlanType planType;

    private User user;
    private Subscription subscription;

    private Integer maxMedications;
    private boolean managedRelation;
    private boolean refillReminders;
    private boolean reminderPreference;

    public Plan(String id,
                PlanType planType,
                Integer maxMedications,
                boolean managedRelation,
                boolean refillReminders,
                boolean reminderPreference) {
        this.id = id;
        this.planType = planType;
        this.maxMedications = maxMedications;
        this.managedRelation = managedRelation;
        this.refillReminders = refillReminders;
        this.reminderPreference = reminderPreference;
    }

    public String getId() {
        return id;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public User getUser() {
        return user;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public Integer getMaxMedications() {
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

    public void setUser(User user) {
        this.user = user;
    }

    public void updatePlanType(PlanType planType) {
        this.planType = planType;
    }

    public void updateMaxMedications(Integer maxMedications) {
        this.maxMedications = maxMedications;
    }

    public void updateManagedRelation(boolean managedRelation) {
        this.managedRelation = managedRelation;
    }

    public void updateRefillReminders(boolean refillReminders) {
        this.refillReminders = refillReminders;
    }

    public void updateReminderPreference(boolean reminderPreference) {
        this.reminderPreference = reminderPreference;
    }
}