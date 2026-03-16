package com.medreminder.medreminder_server.infrastructure.repository.medications;

import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationScheduleEntity;
import com.medreminder.medreminder_server.infrastructure.repository.BaseJpaRepository;

public interface JpaMedicationScheduleRepository extends BaseJpaRepository<MedicationScheduleEntity, String> {
}
