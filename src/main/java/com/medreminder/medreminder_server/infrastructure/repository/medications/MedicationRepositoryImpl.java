package com.medreminder.medreminder_server.infrastructure.repository.medications;

import com.medreminder.medreminder_server.domain.services.medications.MedicationRepository;
import com.medreminder.medreminder_server.infrastructure.entity.medications.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class MedicationRepositoryImpl implements MedicationRepository {


    private final JpaMedicationProfileRepository jpaMedicationProfileRepository;
    private final JpaMedicationScheduleRepository jpaMedicationScheduleRepository;
    private final JpaScheduleEventRepository jpaScheduleEventRepository;

    public MedicationRepositoryImpl(JpaMedicationProfileRepository jpaMedicationProfileRepository,
                                    JpaMedicationScheduleRepository jpaMedicationScheduleRepository,
                                    JpaScheduleEventRepository jpaScheduleEventRepository) {
        this.jpaMedicationProfileRepository = jpaMedicationProfileRepository;
        this.jpaMedicationScheduleRepository = jpaMedicationScheduleRepository;
        this.jpaScheduleEventRepository = jpaScheduleEventRepository;
    }

    @Override
    public MedicationProfileEntity saveMedicationProfile(MedicationProfileEntity medicationProfileEntity) {
        return jpaMedicationProfileRepository.save(medicationProfileEntity);
    }

    @Override
    public void saveMedicationSchedule(MedicationScheduleEntity medicationScheduleEntity) {
        jpaMedicationScheduleRepository.save(medicationScheduleEntity);
    }

    @Override
    public List<ScheduleEventEntity> getMedicationScheduleByUserAndDate(String userId,
                                                                        LocalDateTime startOfDay,
                                                                        LocalDateTime endOfDay) {
        return  jpaScheduleEventRepository.findByUserAndDate(userId, startOfDay, endOfDay);
    }

    @Override
    public MedicationProfileEntity getMedicationProfileById(String id) {
        return jpaMedicationProfileRepository.findById(id).orElse(null);
    }

    @Override
    public void deletePendingScheduleEvents(List<ScheduleEventEntity> scheduleEvents) {
        jpaScheduleEventRepository.deleteAll(scheduleEvents);
    }
}
