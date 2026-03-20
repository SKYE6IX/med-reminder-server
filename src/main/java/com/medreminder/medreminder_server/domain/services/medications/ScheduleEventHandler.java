package com.medreminder.medreminder_server.domain.services.medications;


import com.medreminder.medreminder_server.domain.models.medication.ScheduleEvent;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationScheduleEntity;
import net.fortuna.ical4j.model.Recur;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class ScheduleEventHandler {

    private static final int EXPANSION_WINDOW_DAYS = 7;
    private final MedicationRepository medicationRepository;
    private final MedicationMapper medicationMapper;

    public ScheduleEventHandler(MedicationRepository medicationRepository,
                                MedicationMapper medicationMapper) {
        this.medicationRepository = medicationRepository;
        this.medicationMapper = medicationMapper;
    }

    public void createScheduleEvent(MedicationScheduleEntity managedSchedule) {

        List<LocalDateTime> dates = getNextScheduledDates(managedSchedule);

        dates.stream()
                .map(date -> {
                    LocalDateTime localDateTime = date.atZone(ZoneId.of(managedSchedule.getTimeZone()))
                            .toLocalDateTime();

                    return new ScheduleEvent(null,
                            managedSchedule.getDoseQuantity(),
                            localDateTime);
                })
                .map(medicationMapper::toEntity)
                .forEach(managedSchedule::addScheduleEvent);

        medicationRepository.saveMedicationSchedule(managedSchedule);
    }

    public void updateScheduleEvent() {}

    private List<LocalDateTime> getNextScheduledDates(MedicationScheduleEntity schedule){

        LocalDateTime periodStart = schedule.getLastExpandedUntil() != null
                ? schedule.getLastExpandedUntil()
                : schedule.getStartDate();

        LocalDateTime windowStart = periodStart.toLocalDate().atStartOfDay();

        LocalDateTime windowEnd = windowStart.plusDays(EXPANSION_WINDOW_DAYS)
                .toLocalDate()
                .atTime(23, 59, 59);

        Recur<LocalDateTime> recur = new Recur<>(schedule.getRecurrenceRule());

        return recur.getDates(windowStart, windowStart, windowEnd);
    }
}






