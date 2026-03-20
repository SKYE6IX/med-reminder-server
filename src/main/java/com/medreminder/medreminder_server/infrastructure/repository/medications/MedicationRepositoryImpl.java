package com.medreminder.medreminder_server.infrastructure.repository.medications;

import com.medreminder.medreminder_server.domain.services.medications.MedicationRepository;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationPackEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationScheduleEntity;
import org.springframework.stereotype.Repository;

@Repository
public class MedicationRepositoryImpl implements MedicationRepository {


    private final JpaMedicationProfileRepository jpaMedicationProfileRepository;
    private final JpaMedicationScheduleRepository jpaMedicationScheduleRepository;

    public MedicationRepositoryImpl(JpaMedicationProfileRepository jpaMedicationProfileRepository,
                                    JpaMedicationScheduleRepository jpaMedicationScheduleRepository) {
        this.jpaMedicationProfileRepository = jpaMedicationProfileRepository;
        this.jpaMedicationScheduleRepository = jpaMedicationScheduleRepository;
    }


    @Override
    public MedicationProfileEntity saveMedicationProfile(MedicationProfileEntity medicationProfileEntity) {
        return jpaMedicationProfileRepository.save(medicationProfileEntity);
    }

    @Override
    public void saveMedicationSchedule(MedicationScheduleEntity medicationScheduleEntity) {

        jpaMedicationScheduleRepository.save(medicationScheduleEntity);
    }

}
