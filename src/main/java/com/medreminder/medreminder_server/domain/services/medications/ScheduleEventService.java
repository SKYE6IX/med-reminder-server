package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.application.dtos.medication.ScheduleEventResponse;
import com.medreminder.medreminder_server.domain.models.medication.MedicationSchedule;
import com.medreminder.medreminder_server.domain.services.UseCase;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationScheduleEntity;

import java.util.Map;

public interface ScheduleEventService extends UseCase {

    public void createScheduleEvents(MedicationSchedule schedule);

    public void updateScheduleEventsRules(MedicationSchedule schedule);

    public void updateScheduleEventsDosage(MedicationSchedule schedule);

    public ScheduleEventResponse updateScheduleEvents(String scheduleEventId, Map<String, String> eventBody);
}

