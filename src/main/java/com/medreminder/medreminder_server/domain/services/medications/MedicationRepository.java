package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.infrastructure.entity.medications.*;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicationRepository {

    void saveMedicationProfile(MedicationProfileEntity medicationProfileEntity);

    MedicationProfileEntity getMedicationProfileById(String id);

    void saveScheduleEvent(ScheduleEventEntity scheduleEvent);

    ScheduleEventEntity getScheduleEventById(String id);

    List<ScheduleEventEntity> getScheduleEventsByUserIdAndDates(String userId,
                                                                LocalDateTime startOfDay,
                                                                LocalDateTime endOfDay);
}
