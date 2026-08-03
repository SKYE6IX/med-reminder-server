package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.application.dtos.medication.LogScheduleEventRequest;
import com.medreminder.medreminder_server.application.dtos.medication.ScheduleEventResponse;
import com.medreminder.medreminder_server.domain.models.medication.MedicationSchedule;
import com.medreminder.medreminder_server.domain.models.medication.ScheduleEvent;

import java.util.List;
import java.util.Map;

public interface ScheduleEventService {

    List<ScheduleEvent> createScheduleEvents(MedicationSchedule schedule, String timeZone);

    List<ScheduleEvent> updateScheduleEventsRule(MedicationSchedule schedule, String timeZone);

    ScheduleEventResponse logScheduleEvent(String scheduleEventId, LogScheduleEventRequest eventBody);

    void logOverdueScheduleEvent(String userId, Map<String, String> eventBody);

    List<ScheduleEventResponse> getScheduleEvents(String userId, String eventDate);

    List<ScheduleEventResponse> getUpcomingScheduleEvents(String userId,
                                                  String eventDateFrom,
                                                  int limit);
}