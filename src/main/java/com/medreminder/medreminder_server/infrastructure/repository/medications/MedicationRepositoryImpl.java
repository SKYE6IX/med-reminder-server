package com.medreminder.medreminder_server.infrastructure.repository.medications;

import com.medreminder.medreminder_server.domain.services.medications.MedicationRepository;
import com.medreminder.medreminder_server.infrastructure.entity.medications.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class MedicationRepositoryImpl implements MedicationRepository {


    private final JpaMedicationProfileRepository jpaMedicationProfileRepository;
    private final JpaScheduleEventRepository jpaScheduleEventRepository;

    public MedicationRepositoryImpl(JpaMedicationProfileRepository jpaMedicationProfileRepository,
                                    JpaScheduleEventRepository jpaScheduleEventRepository) {
        this.jpaMedicationProfileRepository = jpaMedicationProfileRepository;
        this.jpaScheduleEventRepository = jpaScheduleEventRepository;
    }



    @Override
    public void saveMedicationProfile(MedicationProfileEntity medicationProfileEntity) {
        jpaMedicationProfileRepository.save(medicationProfileEntity);
    }

    @Override
    public MedicationProfileEntity getMedicationProfileById(String id) {

        return jpaMedicationProfileRepository
                .findByIdWithScheduleAndEvents(id).orElse(null);
    }

    @Override
    public List<MedicationProfileEntity> getAllMedicationProfilesByUserId(String userId) {
        return jpaMedicationProfileRepository.findAllByUserId(userId);
    }

    @Override
    public void saveScheduleEvent(ScheduleEventEntity scheduleEvent) {
        jpaScheduleEventRepository.save(scheduleEvent);
    }

    @Override
    public ScheduleEventEntity getScheduleEventById(String id) {
        return jpaScheduleEventRepository.findByIdWithDetails(id)
                .orElse(null);
    }

    @Override
    public List<ScheduleEventEntity> getScheduleEventsByUserIdAndDates(String userId,
                                                                       LocalDateTime startOfDay,
                                                                       LocalDateTime endOfDay) {
        return  jpaScheduleEventRepository.findByUserIdAndDates(userId, startOfDay, endOfDay);
    }
}
