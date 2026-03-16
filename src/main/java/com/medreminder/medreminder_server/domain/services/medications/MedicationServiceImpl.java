package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.application.dtos.medication.CreateMedPack;
import com.medreminder.medreminder_server.application.dtos.medication.CreateMedSchedule;
import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationCommand;
import com.medreminder.medreminder_server.domain.models.medication.*;
import com.medreminder.medreminder_server.domain.services.users.ProfileRepository;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationPackEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationScheduleEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;

import java.time.LocalDateTime;

public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository medicationRepository;
    private final ProfileRepository profileRepository;
    private final MedicationMapper medicationMapper;

    public MedicationServiceImpl(MedicationRepository medicationRepository,
                                 ProfileRepository profileRepository) {
        this.medicationRepository = medicationRepository;
        this.profileRepository = profileRepository;
        this.medicationMapper = new MedicationMapper();
    }

    @Override
    public void createMedication(String profileId, CreateMedicationCommand cmd) {

        ProfileEntity profileEntity = profileRepository.findProfileById(profileId).orElse(null);

        if (profileEntity == null) {
            return;
        }

//        We need to delegate the creation of new medicine to a private method ✅
//        Then we also do the same thing to schedule creation too ✅
//        And last the packs, if the value of the pack was included. For this
//        we need to make the return getMedPacks to be optional. and run some
//        function is they are present.
    }


    private MedicationEntity createMedicationEntity(CreateMedicationCommand cmd) {

        MeasurementUnit measurementUnit = new MeasurementUnit(null,
                Measurement.valueOf(cmd.getMedicationMeasurement()));

        Medication medication = new Medication(null, cmd.getMedicationName(),
                Unit.valueOf(cmd.getMedicationUnit()), measurementUnit);

        return medicationRepository.saveMedication(medicationMapper.toEntity(medication));
    }

    private MedicationScheduleEntity createMedicationScheduleEntity(CreateMedSchedule schedule) {

        MedicationSchedule medicationSchedule = new MedicationSchedule(null,
                schedule.dosage(), schedule.recurrenceRule(), LocalDateTime.parse(schedule.startAt()));

        return medicationRepository.saveMedicationSchedule(medicationMapper.toEntity(medicationSchedule));
    }

    private MedicationPackEntity createMedicationPack (CreateMedPack pack) {

        if(pack == null) {
            return null;
        }

        MedicationPack medicationPack = new MedicationPack(null,
                pack.totalQuantity(), pack.notifyRule(), LocalDateTime.now());

        return null;
    }
}