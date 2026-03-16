package com.medreminder.medreminder_server.infrastructure.repository.medications;

import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationEntity;
import com.medreminder.medreminder_server.infrastructure.repository.BaseJpaRepository;

public interface JpaMedicationRepository extends BaseJpaRepository<MedicationEntity, String> {
}
