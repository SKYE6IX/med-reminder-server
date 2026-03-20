package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.infrastructure.entity.medications.*;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicationRepository {

    MedicationProfileEntity saveMedicationProfile(MedicationProfileEntity medicationProfileEntity);

    void saveMedicationSchedule(MedicationScheduleEntity medicationScheduleEntity);

    List<ScheduleEventEntity> getMedicationScheduleByUserAndDate(String userId,
                                                                 LocalDateTime startOfDay,
                                                                 LocalDateTime endOfDay);
}
