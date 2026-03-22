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
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

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


//        dates.stream()
//                .map(date -> {
//                    LocalDateTime localDateTime = date.atZone(ZoneId.of(managedSchedule.getTimeZone()))
//                            .toLocalDateTime();
//
//                    return new ScheduleEvent(null,
//                            managedSchedule.getDoseQuantity(),
//                            localDateTime);
//
//                })
//                .map(medicationMapper::toEntity)
//
//                .forEach(managedSchedule::addScheduleEvent);
//
//        medicationRepository.saveMedicationSchedule(managedSchedule);
    }


//    What should happen when a new rules set?
//      On creation of the rules, we use the rules to set up multiple events that cross span
//      7 days. Changing the rules mean we need to update the pending events
//      (which refer to the events that are yet to happen). But then, what field is it we
//      need to actually update? -> scheduleAt.
//      But this field hold two info, the days(date) and the time.
//      Let assume we've already created multiple days which are -> twice a day at 2pm and 6pm;
//      day1, day2, day3, day4, day5, day6, day7.
//      day1 ✅ at 2pm, (it also possible user change the rules before the second time frame came in) 6pm
//      day2 ✅ at 2pm, 6pm
//      day3 not done
//      day4 not done
//      day5 not done
//      .....
//
//
//    What should happen when a new dosage is set?
//    What should happen when a new start time is set?

    @Override
    public void updateScheduleEvent(String newRules, MedicationScheduleEntity managedSchedule) {

        MedicationSchedule domainSchedule = medicationMapper.toDomain(managedSchedule);
        domainSchedule.updateRecurrenceRule(newRules);

        managedSchedule.updateMedicationSchedule(domainSchedule);

        List<ScheduleEventEntity> pendingEvents = managedSchedule
                .getScheduleEvents().stream()
                .filter(event -> event.getStatus().equals("PENDING"))
                .toList();

        List<LocalDate> pendingDates = pendingEvents.stream()
                .map(event -> event.getScheduleAt().toLocalDate())
                .distinct()
                .sorted()
                .toList();

        medicationRepository.deletePendingScheduleEvents(pendingEvents);

//        List<LocalDateTime> dates = getNextScheduledDates(managedSchedule, pendingDates.size());
//
//        pendingDates.stream()
//                .flatMap(pendingDate -> dates.stream()
//                        .map(date -> new ScheduleEvent(null,
//                                managedSchedule.getDoseQuantity(),
//                                LocalDateTime.of(pendingDate, date.toLocalTime())))
//                        .map(medicationMapper::toEntity)
//                ).forEach(managedSchedule::addScheduleEvent);
//        medicationRepository.saveMedicationSchedule(managedSchedule);
    }

    @Override
    public void updateScheduleEvent(Double newDosage, MedicationScheduleEntity managedSchedule) {

    }

    @Override
    public void updateScheduleEvent(LocalDateTime newStartTime, MedicationScheduleEntity managedSchedule) {

    }

    private List<LocalDateTime> getNextScheduledDates(MedicationScheduleEntity managedSchedule,
                                                      int expansionWindowDays) {

        LocalDateTime periodStart = managedSchedule.getLastExpandedUntil() != null
                ? managedSchedule.getLastExpandedUntil()
                : managedSchedule.getStartDate();

        LocalDateTime windowStart = periodStart.toLocalDate().atStartOfDay();

        LocalDateTime windowEnd = windowStart.plusDays(expansionWindowDays)
                .toLocalDate()
                .atTime(23, 59, 59);

        Recur<LocalDateTime> recur = new Recur<>(managedSchedule.getRecurrenceRule());

        return recur.getDates(windowStart, windowStart, windowEnd);
    }
}





