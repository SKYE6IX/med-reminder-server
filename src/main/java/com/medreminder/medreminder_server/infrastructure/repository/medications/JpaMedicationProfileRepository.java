package com.medreminder.medreminder_server.infrastructure.repository.medications;

import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationProfileEntity;
import com.medreminder.medreminder_server.infrastructure.repository.BaseJpaRepository;

public interface JpaMedicationProfileRepository extends BaseJpaRepository<MedicationProfileEntity, String> {
}
