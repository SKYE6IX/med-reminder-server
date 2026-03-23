package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationScheduleEntity;

import java.time.LocalDateTime;

public interface ScheduleEventService {

    public void createScheduleEvent(MedicationScheduleEntity managedSchedule);

    public void updateScheduleEvent(String newRules, MedicationScheduleEntity managedSchedule);

    public void updateScheduleEvent(Double newDosage, MedicationScheduleEntity managedSchedule);
}

