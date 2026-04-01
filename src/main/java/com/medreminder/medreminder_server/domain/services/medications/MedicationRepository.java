package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.infrastructure.entity.medications.*;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicationRepository {

    void saveMedicationProfile(MedicationProfileEntity medicationProfileEntity);

    void saveMedicationSchedule(MedicationScheduleEntity medicationScheduleEntity);

    void saveAllScheduleEvents(List<ScheduleEventEntity> scheduleEvents);

    void saveScheduleEvent(ScheduleEventEntity scheduleEvent);

    void deleteAllScheduleEvents(List<ScheduleEventEntity> scheduleEvents);

    List<ScheduleEventEntity> getScheduleEventsByUserAndDate(String userId,
                                                             LocalDateTime startOfDay,
                                                             LocalDateTime endOfDay);

    MedicationProfileEntity getMedicationProfileById(String id);

    ScheduleEventEntity getScheduleEventById(String id);
}
