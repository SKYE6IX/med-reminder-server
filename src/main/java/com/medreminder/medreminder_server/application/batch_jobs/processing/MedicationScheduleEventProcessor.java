package com.medreminder.medreminder_server.application.batch_jobs.processing;

import com.medreminder.medreminder_server.domain.models.medication.MedicationSchedule;
import com.medreminder.medreminder_server.domain.services.medications.ScheduleEventService;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationScheduleEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.ScheduleEventEntity;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class MedicationScheduleEventProcessor implements ItemProcessor<MedicationScheduleEntity,
        MedicationScheduleEntity> {

    private static final Logger log = LoggerFactory
            .getLogger(MedicationScheduleEventProcessor.class);

    private final ScheduleEventService scheduleEventService;
    private final MedicationMapper medicationMapper;

    public MedicationScheduleEventProcessor(ScheduleEventService scheduleEventService,
                                            MedicationMapper medicationMapper) {
        this.scheduleEventService = scheduleEventService;
        this.medicationMapper = medicationMapper;
    }

    @Override
    public MedicationScheduleEntity process(MedicationScheduleEntity scheduleEntity) {

        MedicationSchedule domainMedicationSchedule = medicationMapper.toDomain(scheduleEntity);

        List<ScheduleEventEntity> newWindowEvents = scheduleEventService
                .createScheduleEvents(domainMedicationSchedule)
                .stream()
                .map(localEvent -> medicationMapper.toEntity(localEvent,
                        scheduleEntity))
                .toList();

        scheduleEntity.updateMedicationSchedule(domainMedicationSchedule);

        scheduleEntity.getScheduleEvents().addAll(newWindowEvents);

        log.info("Added new schedule events window ({})", newWindowEvents);
        return scheduleEntity;
    }
}