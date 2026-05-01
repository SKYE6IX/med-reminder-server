package com.medreminder.medreminder_server.infrastructure.repository.medications;

import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationProfileEntity;
import com.medreminder.medreminder_server.infrastructure.repository.BaseJpaRepository;
import org.jspecify.annotations.NullUnmarked;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaMedicationProfileRepository extends BaseJpaRepository<MedicationProfileEntity, String> {

    @Query("""
        SELECT mp FROM MEDICATION_PROFILES mp
        JOIN mp.medicationSchedule ms
        JOIN ms.scheduleEvents
        WHERE mp.id = :id
        """)
    Optional<MedicationProfileEntity> findByIdWithScheduleAndEvents(@Param("id") String id);

    @Query("""
        SELECT mp FROM MEDICATION_PROFILES mp
        JOIN mp.profile p
        JOIN p.user u
        WHERE u.id = :userId
        """)
    List<MedicationProfileEntity> findAllByUserId(@Param("userId") String userId);
}
