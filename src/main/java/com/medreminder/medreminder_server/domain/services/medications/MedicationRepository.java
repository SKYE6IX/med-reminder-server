package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.infrastructure.entity.medications.*;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicationRepository {

    MedicationProfileEntity saveMedicationProfile(MedicationProfileEntity medicationProfileEntity);

    MedicationProfileEntity getMedicationProfileById(String id);

    List<MedicationProfileEntity> getAllMedicationProfilesByUserId(String userId);

    void saveScheduleEvent(ScheduleEventEntity scheduleEvent);

    void saveAllScheduledEvent(List<ScheduleEventEntity > scheduleEvents);

    ScheduleEventEntity getScheduleEventById(String id);

    List<ScheduleEventEntity> getScheduleEvents(String userId,
                                                LocalDateTime startOfDay,
                                                LocalDateTime endOfDay);

    List<ScheduleEventEntity> getUpcomingScheduleEvents(String userId,
                                                        LocalDateTime eventDateFrom,
                                                        int limit);

    List<ScheduleEventEntity> getOverdueScheduleEvents(String userId,
                                                       LocalDateTime eventDateFrom);

    List<MedicationPackEntity> getAllMedicationPacksByUserId(String userId);
}
