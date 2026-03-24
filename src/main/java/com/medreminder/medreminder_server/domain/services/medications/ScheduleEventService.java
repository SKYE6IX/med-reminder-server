package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.application.dtos.medication.ScheduleEventResponse;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationScheduleEntity;

import java.time.LocalDateTime;
import java.util.Map;

public interface ScheduleEventService {

    public void createScheduleEvent(MedicationScheduleEntity managedSchedule);

    public void updateScheduleEvent(String newRules, MedicationScheduleEntity managedSchedule);

    public void updateScheduleEvent(Double newDosage, MedicationScheduleEntity managedSchedule);

    public ScheduleEventResponse updateScheduleEvent(String scheduleEventId, Map<String, String> eventBody);
}

