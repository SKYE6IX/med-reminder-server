package com.medreminder.medreminder_server.application.batch_jobs.processing;

import com.medreminder.medreminder_server.domain.models.medication.ScheduleEvent;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.ScheduleEventEntity;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class MarkMissedDosageProcessor implements ItemProcessor<ScheduleEventEntity,
        ScheduleEventEntity> {
    private final MedicationMapper medicationMapper;

    public MarkMissedDosageProcessor(MedicationMapper medicationMapper) {
        this.medicationMapper = medicationMapper;
    }

    @Override
    public ScheduleEventEntity process(ScheduleEventEntity scheduleEvent){
        ScheduleEvent domainScheduleEvent = medicationMapper.toDomain(scheduleEvent);
        domainScheduleEvent.updateStatus("MISSED");
        scheduleEvent.updateScheduleEvent(domainScheduleEvent);
        return scheduleEvent;
    }
}
