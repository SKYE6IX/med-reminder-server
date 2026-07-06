package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.application.dtos.medication.ScheduleEventResponse;
import com.medreminder.medreminder_server.domain.models.medication.MedicationSchedule;
import com.medreminder.medreminder_server.domain.models.medication.ScheduleEvent;

import java.util.List;
import java.util.Map;

public interface ScheduleEventService {

    List<ScheduleEvent> createScheduleEvents(MedicationSchedule schedule);

    List<ScheduleEvent> updateScheduleEventsRule(MedicationSchedule schedule);

    ScheduleEventResponse logScheduleEvent(String scheduleEventId, Map<String, String> eventBody);

    void logOverdueScheduleEvent(String userId, Map<String, String> eventBody);

    List<ScheduleEventResponse> getScheduleEvents(String userId, String eventDate);

    List<ScheduleEventResponse> getUpcomingScheduleEvents(String userId,
                                                  String eventDateFrom,
                                                  int limit);
}