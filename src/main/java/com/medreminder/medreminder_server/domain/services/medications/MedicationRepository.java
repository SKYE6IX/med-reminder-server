package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationPackEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationScheduleEntity;

public interface MedicationRepository {

    MedicationEntity saveMedication(MedicationEntity medicationEntity);

    MedicationProfileEntity saveMedicationProfile(MedicationProfileEntity medicationProfileEntity);

    MedicationScheduleEntity saveMedicationSchedule(MedicationScheduleEntity medicationScheduleEntity);

    MedicationPackEntity saveMedicationPack(MedicationPackEntity medicationPackEntity);
}
