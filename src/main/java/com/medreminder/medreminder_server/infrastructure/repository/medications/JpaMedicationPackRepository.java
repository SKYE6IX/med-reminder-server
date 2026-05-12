package com.medreminder.medreminder_server.infrastructure.repository.medications;

import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationPackEntity;
import com.medreminder.medreminder_server.infrastructure.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaMedicationPackRepository extends BaseJpaRepository<MedicationPackEntity, String> {

    @Query("""
    SELECT packs FROM MEDICATION_PACKS packs
    JOIN packs.medicationProfile mp
    JOIN mp.medicationSchedule
    JOIN mp.medication
    JOIN mp.profile p
    JOIN p.user u
    WHERE u.id = :userId
    """)
    List<MedicationPackEntity> findAllByUserId(@Param("userId") String userId);
}