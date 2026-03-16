package com.medreminder.medreminder_server.infrastructure.repository.medications;

import com.medreminder.medreminder_server.domain.services.medications.MedicationRepository;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationPackEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationScheduleEntity;
import org.springframework.stereotype.Repository;

@Repository
public class MedicationRepositoryImpl implements MedicationRepository {


    private final JpaMedicationRepository jpaMedicationRepository;
    private final JpaMedicationProfileRepository jpaMedicationProfileRepository;
    private final JpaMedicationScheduleRepository jpaMedicationScheduleRepository;
    private final JpaMedicationPackRepository jpaMedicationPackRepository;

    public MedicationRepositoryImpl(JpaMedicationRepository jpaMedicationRepository,
                                    JpaMedicationProfileRepository jpaMedicationProfileRepository,
                                    JpaMedicationScheduleRepository jpaMedicationScheduleRepository,
                                    JpaMedicationPackRepository jpaMedicationPackRepository) {
        this.jpaMedicationRepository = jpaMedicationRepository;
        this.jpaMedicationProfileRepository = jpaMedicationProfileRepository;
        this.jpaMedicationScheduleRepository = jpaMedicationScheduleRepository;
        this.jpaMedicationPackRepository = jpaMedicationPackRepository;
    }


    @Override
    public MedicationEntity saveMedication(MedicationEntity medicationEntity) {

        return jpaMedicationRepository.save(medicationEntity);

    }

    @Override
    public MedicationProfileEntity saveMedicationProfile(MedicationProfileEntity medicationProfileEntity) {
        return jpaMedicationProfileRepository.save(medicationProfileEntity);
    }

    @Override
    public MedicationScheduleEntity saveMedicationSchedule(MedicationScheduleEntity medicationScheduleEntity) {

        return jpaMedicationScheduleRepository.save(medicationScheduleEntity);
    }

    @Override
    public MedicationPackEntity saveMedicationPack(MedicationPackEntity medicationPackEntity) {
        return null;
    }
}
