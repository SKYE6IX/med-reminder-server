package com.medreminder.medreminder_server.infrastructure.repository.medications;

import com.medreminder.medreminder_server.domain.services.medications.MedicationRepository;
import com.medreminder.medreminder_server.infrastructure.entity.medications.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class MedicationRepositoryImpl implements MedicationRepository {

    private final JpaMedicationProfileRepo jpaMedicationProfileRepo;
    private final JpaScheduleEventRepo jpaScheduleEventRepo;
    private final JpaMedicationPackRepo jpaMedicationPackRepo;

    public MedicationRepositoryImpl(JpaMedicationProfileRepo jpaMedicationProfileRepo,
                                    JpaScheduleEventRepo jpaScheduleEventRepo,
                                    JpaMedicationPackRepo jpaMedicationPackRepo) {
        this.jpaMedicationProfileRepo = jpaMedicationProfileRepo;
        this.jpaScheduleEventRepo = jpaScheduleEventRepo;
        this.jpaMedicationPackRepo = jpaMedicationPackRepo;
    }

    @Override
    public MedicationProfileEntity saveMedicationProfile(MedicationProfileEntity medicationProfileEntity) {
        return jpaMedicationProfileRepo.save(medicationProfileEntity);
    }

    @Override
    public MedicationProfileEntity getMedicationProfileById(String id) {

        return jpaMedicationProfileRepo
                .findByIdWithScheduleAndEvents(id).orElse(null);
    }

    @Override
    public List<MedicationProfileEntity> getAllMedicationProfilesByUserId(String userId) {
        return jpaMedicationProfileRepo.findAllByUserId(userId);
    }

    @Override
    public void saveScheduleEvent(ScheduleEventEntity scheduleEvent) {
        jpaScheduleEventRepo.save(scheduleEvent);
    }

    @Override
    public ScheduleEventEntity getScheduleEventById(String id) {
        return jpaScheduleEventRepo.findByIdWithDetails(id)
                .orElse(null);
    }

    @Override
    public List<ScheduleEventEntity> getScheduleEventsByUserIdAndDates(String userId,
                                                                       LocalDateTime startOfDay,
                                                                       LocalDateTime endOfDay) {
        return  jpaScheduleEventRepo.findByUserIdAndDates(userId, startOfDay, endOfDay);
    }

    @Override
    public List<MedicationPackEntity> getAllMedicationPacksByUserId(String userId) {

        return jpaMedicationPackRepo.findAllByUserId(userId);
    }

}
