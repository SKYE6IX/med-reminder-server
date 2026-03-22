package com.medreminder.medreminder_server.infrastructure.repository.medications;

import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationProfileEntity;
import com.medreminder.medreminder_server.infrastructure.repository.BaseJpaRepository;
import org.jspecify.annotations.NullUnmarked;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JpaMedicationProfileRepository extends BaseJpaRepository<MedicationProfileEntity, String> {

}
