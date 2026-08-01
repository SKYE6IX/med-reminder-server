package com.medreminder.medreminder_server.infrastructure.repository.medications;

import com.medreminder.medreminder_server.infrastructure.entity.medications.ScheduleEventEntity;
import com.medreminder.medreminder_server.infrastructure.repository.BaseJpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaScheduleEventRepo extends BaseJpaRepository<ScheduleEventEntity, String> {

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
        JOIN se.medicationSchedule s
        JOIN s.medicationProfile mp
        JOIN mp.profile p
        JOIN p.user u
        WHERE u.id = :userId
        AND mp.isActive = true
        AND se.scheduleAt >= :eventDayFrom
        AND se.status = 'PENDING'
        ORDER BY se.scheduleAt ASC
        """)
    List<ScheduleEventEntity> findUpcomingEvents(@Param("userId") String userId,
                                                 @Param("eventDayFrom") LocalDateTime eventDayFrom,
                                                 Pageable pageable
                                                         );
    @Query("""
    SELECT se FROM SCHEDULE_EVENTS se
    JOIN se.medicationSchedule s
    JOIN s.medicationProfile mp
    JOIN mp.profile p
    JOIN p.user u
    WHERE u.id = :userId
    AND se.status = 'PENDING'
    AND se.scheduleAt < :eventDayUntil
    """)
    List<ScheduleEventEntity> findOverdueEvents(@Param("userId") String userId,
                                                @Param("eventDayUntil") LocalDateTime eventDayUntil);

    @Query("""
    SELECT se FROM SCHEDULE_EVENTS se
    JOIN FETCH se.medicationSchedule s
    JOIN FETCH s.medicationProfile mp
    WHERE se.id = :id
    """)
    Optional<ScheduleEventEntity> findByIdWithDetails(@Param("id") String id);
}