package com.medreminder.medreminder_server.application.services;


import com.medreminder.medreminder_server.domain.models.medication.MedicationSchedule;
import com.medreminder.medreminder_server.domain.models.medication.ScheduleEvent;
import com.medreminder.medreminder_server.domain.services.medications.MedicationRepository;
import com.medreminder.medreminder_server.domain.services.medications.ScheduleEventService;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationScheduleEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.ScheduleEventEntity;
import net.fortuna.ical4j.model.Recur;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ScheduleEventServiceImpl implements ScheduleEventService {

    private final MedicationRepository medicationRepository;
    private final MedicationMapper medicationMapper;

    public ScheduleEventServiceImpl(MedicationRepository medicationRepository,
                                    MedicationMapper medicationMapper) {
        this.medicationRepository = medicationRepository;
        this.medicationMapper = medicationMapper;
    }

    @Override
    public void createScheduleEvent(MedicationScheduleEntity managedSchedule) {

        List<LocalDateTime> dates = getNextScheduledDates(managedSchedule, 7);

        LocalDateTime startTime = dates.stream().sorted().findFirst().orElse(null);

        managedSchedule.addStartTime(startTime);

        dates.stream()
                .sorted()
                .map(date -> {
                    return new ScheduleEvent(null,
                            managedSchedule.getDoseQuantity(),
                            date);

                })
                .map(medicationMapper::toEntity)
                .forEach(managedSchedule::addScheduleEvent);

        medicationRepository.saveMedicationSchedule(managedSchedule);
    }

    @Override
    public void updateScheduleEvent(String newRules, MedicationScheduleEntity managedSchedule) {

        MedicationSchedule domainSchedule = medicationMapper.toDomain(managedSchedule);

        domainSchedule.updateRecurrenceRule(newRules);

        managedSchedule.updateMedicationSchedule(domainSchedule);

//      Collect all the pending event into list for deletion.
        List<ScheduleEventEntity> pendingEvents = managedSchedule
                .getScheduleEvents().stream()
                .filter(event -> event.getStatus().equals("PENDING"))
                .toList();

        if (pendingEvents.isEmpty()) {
            medicationRepository.saveMedicationSchedule(managedSchedule);
            return;
        }

//        Passed all the above pending events to dates so it can be reused for the schedule date plus
//        either a new Time or Same as the previous Time. Depend on the User's new selection.
        List<LocalDate> pendingDates = pendingEvents.stream()
                .map(event -> event.getScheduleAt().toLocalDate())
                .distinct()
                .sorted()
                .toList();
        int daysLeft = (int) ChronoUnit.DAYS.between(pendingDates.getFirst(), pendingDates.getLast());

        managedSchedule.getScheduleEvents().removeAll(pendingEvents);

//        Get the new dates base on the value user passed.
        List<LocalTime> newTimes = getNextScheduledDates(managedSchedule, daysLeft)
                .stream()
                .map(LocalDateTime::toLocalTime)
                .distinct()
                .sorted()
                .toList();

//        Always sync start time with the new updates of the "nextSchedules"
        managedSchedule.addStartTime(LocalDateTime.of(pendingDates.getFirst(), newTimes.getFirst()));

        pendingDates.stream()
                .flatMap(pendingDate -> newTimes.stream()
                        .map(time -> new ScheduleEvent(null,
                                managedSchedule.getDoseQuantity(),
                                LocalDateTime.of(pendingDate, time)))
                        .map(medicationMapper::toEntity)
                )
                .forEach(managedSchedule::addScheduleEvent);
        medicationRepository.saveMedicationSchedule(managedSchedule);
    }

    @Override
    public void updateScheduleEvent(Double newDosage, MedicationScheduleEntity managedSchedule) {

        MedicationSchedule domainSchedule = medicationMapper.toDomain(managedSchedule);

        domainSchedule.updateDoseQuantity(newDosage);

        managedSchedule.updateMedicationSchedule(domainSchedule);

        List<ScheduleEventEntity> pendingEvents = managedSchedule
                .getScheduleEvents().stream()
                .filter(event -> event.getStatus().equals("PENDING"))
                .map(medicationMapper::toDomain)
                .peek(event -> event.updateDosage(newDosage))
                .map(domainEvent -> {
                    ScheduleEventEntity see = medicationMapper.toEntity(domainEvent);
                    see.updateScheduleEvent(domainEvent);
                    see.addMedicationSchedule(managedSchedule);
                    return see;
                })
                .toList();

        medicationRepository.savePendingScheduleEvents(pendingEvents);

        medicationRepository.saveMedicationSchedule(managedSchedule);
    }

    private List<LocalDateTime> getNextScheduledDates(MedicationScheduleEntity managedSchedule,
                                                        int expansionWindowDays) {
        ZoneId zoneId = ZoneId.of(managedSchedule.getTimeZone());

        LocalDateTime periodStart = managedSchedule.getLastExpandedUntil() != null ?
                managedSchedule.getLastExpandedUntil().atZone(zoneId).toLocalDateTime()
                : managedSchedule.getStartDate().atZone(zoneId).toLocalDateTime();

        LocalDateTime windowStart = periodStart.toLocalDate().atStartOfDay();

        LocalDateTime windowEnd = windowStart.plusDays(expansionWindowDays - 1)
                .toLocalDate()
                .atTime(23, 59, 59);

        Recur<LocalDateTime> recur = new Recur<>(managedSchedule.getRecurrenceRule());

        return recur.getDates(windowStart, windowStart, windowEnd);
    }
}





