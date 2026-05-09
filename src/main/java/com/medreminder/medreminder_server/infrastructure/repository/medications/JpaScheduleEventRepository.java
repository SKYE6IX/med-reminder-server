package com.medreminder.medreminder_server.infrastructure.repository.medications;

import com.medreminder.medreminder_server.infrastructure.entity.medications.ScheduleEventEntity;
import com.medreminder.medreminder_server.infrastructure.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaScheduleEventRepository extends BaseJpaRepository<ScheduleEventEntity, String> {

    @Query("""
        SELECT se FROM SCHEDULE_EVENTS se
        JOIN se.medicationSchedule s
        JOIN s.medicationProfile mp
        JOIN mp.profile p
        JOIN p.user u
        WHERE u.id = :userId
        AND mp.isActive = true
        AND se.scheduleAt >= :startOfDay
        AND se.scheduleAt < :endOfDay
        """)
    List<ScheduleEventEntity> findByUserIdAndDates(@Param("userId") String userId,
                                                   @Param("startOfDay") LocalDateTime startOfDay,
                                                   @Param("endOfDay") LocalDateTime endOfDay);

    @Query("""
    SELECT se FROM SCHEDULE_EVENTS se
    JOIN FETCH se.medicationSchedule s
    JOIN FETCH s.medicationProfile mp
    WHERE se.id = :id
    """)
    Optional<ScheduleEventEntity> findByIdWithDetails(@Param("id") String id);
}